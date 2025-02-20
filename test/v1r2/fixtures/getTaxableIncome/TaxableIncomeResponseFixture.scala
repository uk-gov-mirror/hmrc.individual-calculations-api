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

package v1r2.fixtures.getTaxableIncome

import play.api.libs.json.{JsObject, JsValue, Json}
import v1r2.fixtures.getMetadata.MetadataResponseFixture._
import v1r2.fixtures.getTaxableIncome.detail.CalculationDetailFixture._
import v1r2.fixtures.getTaxableIncome.summary.CalculationSummaryFixture._
import v1r2.models.response.getTaxableIncome.TaxableIncomeResponse

object TaxableIncomeResponseFixture {

  val taxableIncomeResponseModel: TaxableIncomeResponse =
    TaxableIncomeResponse(
      summary = calculationSummaryModel,
      detail = calculationDetailModel,
      id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    )

  val taxableIncomeResponseJson: JsValue = Json.parse(
    s"""
       |{
       |   "summary": $calculationSummaryJson,
       |   "detail": $calculationDetailJson
       |}
     """.stripMargin
  )

  val taxableIncomeResponseTopLevelJson: JsValue =
    Json.obj("taxableIncome" -> taxableIncomeResponseJson) ++
      metadataResponseTopLevelJsonWithoutErrors.as[JsObject]
}