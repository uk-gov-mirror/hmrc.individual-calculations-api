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

package v1.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getAllowancesDeductionsAndReliefs.detail.ForeignTaxCreditReliefFixture._
import v1.fixtures.getAllowancesDeductionsAndReliefs.detail.ResidentialFinancialCostsFixture._
import v1.fixtures.getAllowancesDeductionsAndReliefs.detail.PensionContributionReliefsFixture._
import v1.fixtures.getAllowancesDeductionsAndReliefs.detail.ReliefsClaimedFixture._
import v1.models.response.getAllowancesDeductionsAndReliefs.detail.Reliefs

object ReliefsFixture {
  val reliefsModel: Reliefs = Reliefs(
    residentialFinanceCosts = Some(residentialFinancialCostsModel),
    foreignTaxCreditRelief = Some(Seq(foreignTaxCreditReliefModel)),
    pensionContributionReliefs = Some(pensionContributionReliefsModel),
    reliefsClaimed = Some(Seq(reliefsClaimedModel))
  )

  val reliefsJson: JsValue = Json.parse(
    s"""
       |{
       |  "residentialFinanceCosts": $residentialFinancialCostsJson,
       |  "foreignTaxCreditRelief": [$foreignTaxCreditReliefJson],
       |  "pensionContributionReliefs": $pensionContributionReliefsJson,
       |  "reliefsClaimed": [$reliefsClaimedJson]
       |}
    """.stripMargin
  )
}