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

package v1r2.controllers.requestParsers

import javax.inject.Inject
import uk.gov.hmrc.domain.Nino
import v1r2.controllers.requestParsers.validators.IntentToCrystalliseValidator
import v1r2.models.domain.DesTaxYear
import v1r2.models.request.intentToCrystallise.{IntentToCrystalliseRawData, IntentToCrystalliseRequest}

class IntentToCrystalliseRequestParser @Inject()(val validator: IntentToCrystalliseValidator)
  extends RequestParser[IntentToCrystalliseRawData, IntentToCrystalliseRequest] {

  override protected def requestFor(data: IntentToCrystalliseRawData): IntentToCrystalliseRequest =
    IntentToCrystalliseRequest(Nino(data.nino), DesTaxYear.fromMtd(data.taxYear))
}