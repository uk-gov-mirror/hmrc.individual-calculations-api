/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.controllers

import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.handler.{RequestDefn, RequestHandler}
<<<<<<< HEAD
import v1.fixtures.getMetadata.MetadataResponseFixture
=======
import v1.mocks.MockIdGenerator
>>>>>>> 1cfe0a3f2a02146d80c23d4b6db3af2f2dfbc8d9
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockGetCalculationParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.errors._
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}
<<<<<<< HEAD
import v1.models.response.getMetadata.MetadataHateoasData
=======
import v1.models.response.common.{CalculationReason, CalculationRequestor, CalculationType}
import v1.models.response.getMetadata.{MetadataExistence, MetadataHateoasData, MetadataResponse}
>>>>>>> 1cfe0a3f2a02146d80c23d4b6db3af2f2dfbc8d9
import v1.support.BackendResponseMappingSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetMetadataControllerSpec
    extends ControllerBaseSpec
<<<<<<< HEAD
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockGetCalculationParser
    with MockStandardService
    with MockHateoasFactory
    with MockAuditService
    with GraphQLQuery {

  override val query: String = METADATA_QUERY
=======
      with MockEnrolmentsAuthService
      with MockMtdIdLookupService
      with MockGetCalculationParser
      with MockStandardService
      with MockHateoasFactory
      with MockAuditService
      with MockIdGenerator{
>>>>>>> 1cfe0a3f2a02146d80c23d4b6db3af2f2dfbc8d9

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new GetMetadataController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockGetCalculationParser,
      service = mockStandardService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val nino          = "AA123456A"
  private val calcId        = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"

  val links: JsObject = Json.parse("""
      |{
      |    "links": [
      |      {
      |       "href": "/foo/bar",
      |       "method": "GET",
      |       "rel": "test-relationship"
      |      }
      |    ]
<<<<<<< HEAD
      |}""".stripMargin).as[JsObject]
=======
      |}""".stripMargin)

  val response = MetadataResponse(
    id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    taxYear = "2018-19",
    requestedBy = CalculationRequestor.customer,
    calculationReason = CalculationReason.customerRequest,
    calculationTimestamp = Some("2019-11-15T09:35:15.094Z"),
    calculationType = CalculationType.crystallisation,
    intentToCrystallise = true,
    crystallised = false,
    totalIncomeTaxAndNicsDue = None,
    calculationErrorCount = None,
    metadataExistence = None
  )
>>>>>>> 1cfe0a3f2a02146d80c23d4b6db3af2f2dfbc8d9

  val error: ErrorWrapper = ErrorWrapper(correlationId, NotFoundError, None, NOT_FOUND)

  private val rawData     = GetCalculationRawData(nino, calcId)
  private val requestData = GetCalculationRequest(Nino(nino), calcId)

  private def uri = s"/$nino/self-assessment/$calcId"
  private def queryUri = "/input/uri"

  val testHateoasLink = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  "handleRequest" should {
    "return OK the calculation metadata" when {
      "happy path" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.GraphQl(uri, query), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, MetadataResponseFixture.metadataJsonFromBackend))))

        MockHateoasFactory
<<<<<<< HEAD
          .wrap(MetadataResponseFixture.metadataJson(), MetadataHateoasData(nino, calcId, Some(0)))
          .returns(HateoasWrapper(MetadataResponseFixture.metadataJson(), Seq(testHateoasLink)))
=======
          .wrap(response, MetadataHateoasData(nino, calcId, None, MetadataExistence()))
          .returns(HateoasWrapper(response, Seq(testHateoasLink)))
>>>>>>> 1cfe0a3f2a02146d80c23d4b6db3af2f2dfbc8d9

        val result: Future[Result] = controller.getMetadata(nino, calcId)(fakeGetRequest(queryUri))

        val responseBody = MetadataResponseFixture.metadataJson().as[JsObject].deepMerge(links)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> calcId), None, correlationId,
          AuditResponse(OK, None, Some(responseBody)))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationMetadata", "retrieve-self-assessment-tax-calculation-metadata", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
      "happy path with no error count" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.GraphQl(uri, query), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, MetadataResponseFixture.metadataJsonFromBackendWithoutErrorCount))))

        MockHateoasFactory
          .wrap(MetadataResponseFixture.metadataJsonWithoutErrorCount(), MetadataHateoasData(nino, calcId, None))
          .returns(HateoasWrapper(MetadataResponseFixture.metadataJsonWithoutErrorCount(), Seq(testHateoasLink)))

        val result: Future[Result] = controller.getMetadata(nino, calcId)(fakeGetRequest(queryUri))

        val responseBody = MetadataResponseFixture.metadataJsonWithoutErrorCount().as[JsObject].deepMerge(links)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> calcId), None, correlationId,
          AuditResponse(OK, None, Some(responseBody)))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationMetadata", "retrieve-self-assessment-tax-calculation-metadata", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return NOT_FOUND with the correct error message" when {
      "matching resource not found" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.GraphQl(uri, query), OK)
          .returns(Future.successful(Left(error)))

        val result: Future[Result] = controller.getMetadata(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(NotFoundError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> calcId), None, correlationId,
          AuditResponse(NOT_FOUND, Some(List(AuditError(NotFoundError.code))), None))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationMetadata", "retrieve-self-assessment-tax-calculation-metadata", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockGetCalculationParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks: RequestHandler[JsValue, JsValue] => Unit = allChecks[JsValue, JsValue](
        ("FORMAT_NINO", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
        ("FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError, BAD_REQUEST),
        ("MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError, NOT_FOUND),
        ("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR)
      )

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, MetadataResponseFixture.metadataJsonFromBackend))))

      MockHateoasFactory
<<<<<<< HEAD
        .wrap(MetadataResponseFixture.metadataJson(), MetadataHateoasData(nino, calcId, Some(0)))
        .returns(HateoasWrapper(MetadataResponseFixture.metadataJson(), Seq(testHateoasLink)))
=======
        .wrap(response, MetadataHateoasData(nino, calcId, None, MetadataExistence()))
        .returns(HateoasWrapper(response, Seq(testHateoasLink)))
>>>>>>> 1cfe0a3f2a02146d80c23d4b6db3af2f2dfbc8d9

      val result: Future[Result] = controller.getMetadata(nino, calcId)(fakeGetRequest(queryUri))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }
}
