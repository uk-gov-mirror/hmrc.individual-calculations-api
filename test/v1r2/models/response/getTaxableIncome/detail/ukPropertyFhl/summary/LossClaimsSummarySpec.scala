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

package v1r2.models.response.getTaxableIncome.detail.ukPropertyFhl.summary

import support.UnitSpec
import v1r2.fixtures.getTaxableIncome.detail.ukPropertyFhl.summary.LossClaimSummaryFixture._
import v1r2.models.utils.JsonErrorValidators

class LossClaimsSummarySpec extends UnitSpec with JsonErrorValidators {

  testJsonProperties[LossClaimsSummary](lossClaimSummaryJson)(
    mandatoryProperties = Seq(),
    optionalProperties = Seq(
      "lossForCSFHL",
      "totalBroughtForwardIncomeTaxLosses",
      "broughtForwardIncomeTaxLossesUsed",
      "totalIncomeTaxLossesCarriedForward"
    )
  )
}