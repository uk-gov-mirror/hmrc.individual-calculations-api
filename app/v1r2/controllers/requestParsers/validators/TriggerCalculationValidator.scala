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

package v1r2.controllers.requestParsers.validators

import v1r2.controllers.requestParsers.validators.validations.{JsonFormatValidation, NinoValidation, TaxYearNotSupportedValidation, TaxYearValidation}
import v1r2.models.domain.TriggerCalculationRequestBody
import v1r2.models.errors.MtdError
import v1r2.models.request.TriggerCalculationRawData

class TriggerCalculationValidator extends Validator[TriggerCalculationRawData] {

  private val validationSet = List(parserValidation, jsonFormatValidation, bodyFormatValidation, bodyRuleValidation)

  private def parserValidation: TriggerCalculationRawData => List[List[MtdError]] = { data =>
    List(NinoValidation.validate(data.nino))
  }

  private def jsonFormatValidation: TriggerCalculationRawData => List[List[MtdError]] = { data =>
    List(JsonFormatValidation.validate[TriggerCalculationRequestBody](data.body.json))
  }

  private def bodyFormatValidation: TriggerCalculationRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[TriggerCalculationRequestBody]
    List(TaxYearValidation.validate(req.taxYear))
  }

  private def bodyRuleValidation: TriggerCalculationRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[TriggerCalculationRequestBody]
    List(TaxYearNotSupportedValidation.validate(req.taxYear))
  }

  override def validate(data: TriggerCalculationRawData): List[MtdError] = run(validationSet, data).distinct
}