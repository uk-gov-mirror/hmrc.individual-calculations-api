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

package v1r2.fixtures.getTaxableIncome.detail.foreignProperty.detail

import play.api.libs.json.{JsValue, Json}
import v1r2.fixtures.getTaxableIncome.detail.foreignProperty.detail.ClaimsNotAppliedFixture._
import v1r2.fixtures.getTaxableIncome.detail.foreignProperty.detail.DefaultCarriedForwardLossFixture._
import v1r2.fixtures.getTaxableIncome.detail.foreignProperty.detail.LossBroughtForwardFixture._
import v1r2.fixtures.getTaxableIncome.detail.foreignProperty.detail.ResultOfClaimAppliedFixture._
import v1r2.models.response.getTaxableIncome.detail.foreignProperty.detail.LossClaimsDetail

object LossClaimsDetailFixture {

  val lossClaimsDetailModel: LossClaimsDetail =
    LossClaimsDetail(
      lossesBroughtForward = Some(Seq(lossBroughtForwardModel)),
      resultOfClaimsApplied = Some(Seq(resultOfClaimAppliedModel)),
      defaultCarriedForwardLosses = Some(Seq(defaultCarriedForwardLossModel)),
      claimsNotApplied = Some(Seq(claimsNotAppliedModel))
    )

  val lossClaimsDetailJson: JsValue = Json.parse(
    s"""
      |{
      |   "lossesBroughtForward": [$lossBroughtForwardJson],
      |	  "resultOfClaimsApplied": [$resultOfClaimAppliedJson],
      |	  "defaultCarriedForwardLosses": [$defaultCarriedForwardLossJson],
      |	  "claimsNotApplied": [$claimsNotAppliedJson]
      |}
    """.stripMargin
  )
}