package com.projective.ppa

import scala.io._

import geotrellis.vector._
import geotrellis.vector.io.json._

import spray.json._

//{"name":"PENNYPACK_PARK","listname":"Pennypack Park","mapname":"Pennypack Park","shape_leng":87084.2855886,"shape_area":60140755.7554,"cartodb_id":9,"created_at":"2013-03-19T17:41:50.508Z","updated_at":"2013-03-19T17:41:50.743Z"}

case class NeighborhoodData(name: String, listName: String)

object Main {
  implicit object NeighborhoodDataFormat extends RootJsonFormat[NeighborhoodData] {
    def write(x: NeighborhoodData) = ???

    def read(value: JsValue): NeighborhoodData = 
      value.asJsObject.getFields("name", "listname") match {
        case Seq(JsString(name), JsString(listName)) =>
          NeighborhoodData(name, listName)
        case _ => throw new DeserializationException("Couldn't read neighborhood data")
      }
  }


  def read(path: String): String =
    Source.fromFile(path).getLines.mkString

  def main(args: Array[String]): Unit = {
    val txt = 
      read("data/Neighborhoods_Philadelphia.geojson")

    val mps = txt.parseGeoJson[JsonFeatureCollection].getAllMultiPolygonFeatures[NeighborhoodData]

    val p = Point(-75.160014, 39.933901)

    mps.find(_.geom.contains(p)) match {
      case Some(mpf) =>
        println(s"I live in ${mpf.data.name}")
      case None =>
        println(s"I don't live anywhere")
    }
  }
}
