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

package v1r2.fixtures.getTaxableIncome.detail.selfEmployment.detail

import play.api.libs.json.{JsValue, Json}
import v1r2.models.response.common.LossType
import v1r2.models.response.getTaxableIncome.detail.selfEmployment.detail.LossBroughtForward

object LossBroughtForwardFixture {

  val lossType: LossType = LossType.INCOME
  val taxYearLossIncurred: String = "2055-56"
  val currentLossValue: BigDecimal = 673350334
  val mtdLoss: Boolean = false

  val lossBroughtForwardResponse: LossBroughtForward =
    LossBroughtForward(
      lossType = LossType.INCOME,
      taxYearLossIncurred = taxYearLossIncurred,
      currentLossValue = currentLossValue,
      mtdLoss = mtdLoss
    )

  val lossBroughtForwardJson: JsValue = Json.parse(
    s"""
      |{
      |    "lossType": ${Json.toJson(lossType)},
      |    "taxYearLossIncurred": "$taxYearLossIncurred",
      |    "currentLossValue": $currentLossValue,
      |    "mtdLoss": $mtdLoss
      |}
    """.stripMargin
  )
}