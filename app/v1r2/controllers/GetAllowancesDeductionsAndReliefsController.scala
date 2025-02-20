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

package v1r2.controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import utils.IdGenerator
import v1r2.connectors.httpparsers.StandardHttpParser
import v1r2.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1r2.controllers.requestParsers.GetCalculationParser
import v1r2.handler.{AuditHandler, RequestDefn, RequestHandler}
import v1r2.hateoas.HateoasFactory
import v1r2.models.audit.GenericAuditDetail
import v1r2.models.errors._
import v1r2.models.hateoas.HateoasWrapper
import v1r2.models.request.{GetCalculationRawData, GetCalculationRequest}
import v1r2.models.response.calculationWrappers.CalculationWrapperOrError
import v1r2.models.response.getAllowancesDeductionsAndReliefs.{AllowancesDeductionsAndReliefsHateoasData, AllowancesDeductionsAndReliefsResponse}
import v1r2.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetAllowancesDeductionsAndReliefsController @Inject()(authService: EnrolmentsAuthService,
                                                            lookupService: MtdIdLookupService,
                                                            parser: GetCalculationParser,
                                                            service: StandardService,
                                                            hateoasFactory: HateoasFactory,
                                                            auditService: AuditService,
                                                            cc: ControllerComponents,
                                                            idGenerator: IdGenerator,
                                                           )(implicit ec: ExecutionContext)
  extends StandardController[GetCalculationRawData,
    GetCalculationRequest,
    CalculationWrapperOrError[AllowancesDeductionsAndReliefsResponse],
    HateoasWrapper[AllowancesDeductionsAndReliefsResponse],
    AnyContent](authService, lookupService, parser, service, auditService, cc, idGenerator) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "GetAllowancesDeductionsAndReliefsController",
      endpointName = "getAllowancesDeductionsAndReliefs"
    )

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  override def requestHandlerFor(playRequest: Request[AnyContent],
                                 req: GetCalculationRequest): RequestHandler[CalculationWrapperOrError[AllowancesDeductionsAndReliefsResponse], HateoasWrapper[AllowancesDeductionsAndReliefsResponse]] =
    RequestHandler[CalculationWrapperOrError[AllowancesDeductionsAndReliefsResponse]](
      RequestDefn.Get(req.backendCalculationUri))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError
      )
      .mapSuccess { responseWrapper =>
        responseWrapper.mapToEither {
          case CalculationWrapperOrError.ErrorsInCalculation => Left(ErrorWrapper(responseWrapper.correlationId, RuleCalculationErrorMessagesExist, None, FORBIDDEN))
          case CalculationWrapperOrError.CalculationWrapper(calc) => if (calc.isEmpty) Left(ErrorWrapper(responseWrapper.correlationId, NoAllowancesDeductionsAndReliefsExist, None, NOT_FOUND)) else Right(calc)
        }
      }
      .mapSuccessSimple(rawResponse =>
        hateoasFactory.wrap(rawResponse, AllowancesDeductionsAndReliefsHateoasData(req.nino.nino, rawResponse.id)))

  def getAllowancesDeductionsAndReliefs(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationRawData(nino, calculationId)

      val auditHandler: AuditHandler[GenericAuditDetail] = AuditHandler.withoutBody(
        "retrieveSelfAssessmentTaxCalculationAllowanceDeductionAndReliefs",
        "retrieve-self-assessment-tax-calculation-allowance-deduction-reliefs",
        Map("nino" -> nino, "calculationId" -> calculationId), request
      )

      doHandleRequest(rawData, Some(auditHandler))
    }
}