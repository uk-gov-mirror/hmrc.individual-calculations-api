/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1r2.models.response.getTaxableIncome

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v1r2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1r2.models.hateoas.{HateoasData, Link}
import v1r2.models.response.getTaxableIncome.detail.CalculationDetail
import v1r2.models.response.getTaxableIncome.summary.CalculationSummary

case class TaxableIncomeResponse(summary: CalculationSummary, detail: CalculationDetail, id: String)

object TaxableIncomeResponse extends HateoasLinks {

  implicit val writes: OWrites[TaxableIncomeResponse] = new OWrites[TaxableIncomeResponse] {
    def writes(response: TaxableIncomeResponse): JsObject =
      Json.obj(
        "summary" -> response.summary,
        "detail" -> response.detail
      )
  }

  implicit val reads: Reads[TaxableIncomeResponse] = (
    (JsPath \ "taxableIncome" \ "summary").read[CalculationSummary] and
      (JsPath \ "taxableIncome" \ "detail").read[CalculationDetail] and
      (JsPath \ "metadata" \ "id").read[String]) (TaxableIncomeResponse.apply _)

  implicit object LinksFactory extends HateoasLinksFactory[TaxableIncomeResponse, TaxableIncomeHateoasData] {
    override def links(appConfig: AppConfig, data: TaxableIncomeHateoasData): Seq[Link] = {

      Seq(
        getMetadata(appConfig, data.nino, data.calculationId, isSelf = false),
        getTaxableIncome(appConfig, data.nino, data.calculationId, isSelf = true)
      )
    }
  }

}

case class TaxableIncomeHateoasData(nino: String, calculationId: String) extends HateoasData