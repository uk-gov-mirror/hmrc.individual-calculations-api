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

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class GetMetadataControllerISpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, Any] = Map(
    "microservice.services.des.host" -> mockHost,
    "microservice.services.des.port" -> mockPort,
    "microservice.services.individual-calculations.host" -> mockHost,
    "microservice.services.individual-calculations.port" -> mockPort,
    "microservice.services.mtd-id-lookup.host" -> mockHost,
    "microservice.services.mtd-id-lookup.port" -> mockPort,
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "auditing.consumer.baseUri.port" -> mockPort,
    "feature-switch.v1r2.enabled" -> false
  )

  private trait Test {

    val nino          = "AA123456A"
    val correlationId = "X-123"
    val calcId        = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def uri: String = s"/$nino/self-assessment/$calcId"

    def backendUrl: String = uri

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }
  }

  "Calling the get calculation metadata endpoint" should {
    "return a 200 status code" when {

      "valid request is made and no error messages and no metadata is returned" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val successBody: JsObject = Json.parse(
          s"""|{
              |  "metadata": {
              |    "id": "$calcId",
              |    "taxYear": "2018-19",
              |    "requestedBy": "customer",
              |    "calculationReason": "customerRequest",
              |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
              |    "calculationType": "crystallisation",
              |    "intentToCrystallise": true,
              |    "crystallised": false,
              |    "metadataExistence": {
              |     "incomeTaxAndNicsCalculated": false,
              |     "messages": false,
              |     "taxableIncome": false,
              |     "endOfYearEstimate": false,
              |     "allowancesDeductionsAndReliefs": false
              |    }
              |  }
              |}""".stripMargin).as[JsObject]

        val successOutput: JsObject = Json.parse(
          s"""{
             |    "id": "$calcId",
             |    "taxYear": "2018-19",
             |    "requestedBy": "customer",
             |    "calculationReason": "customerRequest",
             |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
             |    "calculationType": "crystallisation",
             |    "intentToCrystallise": true,
             |    "crystallised": false
             |}""".stripMargin).as[JsObject]

        val hateoas: JsObject = Json.parse(
          s"""{
             |    "links": [
             |      {
             |       "href": "/individuals/calculations/$nino/self-assessment/$calcId",
             |       "method": "GET",
             |       "rel": "self"
             |      }
             |    ]
             |}""".stripMargin).as[JsObject]

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successOutput.deepMerge(hateoas)
      }

      "valid request is made and no error messages and all metadata is returned" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val successBody: JsObject = Json.parse(
          s"""|{
              |  "metadata": {
              |    "id": "$calcId",
              |    "taxYear": "2018-19",
              |    "requestedBy": "customer",
              |    "calculationReason": "customerRequest",
              |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
              |    "calculationType": "crystallisation",
              |    "intentToCrystallise": true,
              |    "crystallised": false,
              |    "metadataExistence": {
              |     "incomeTaxAndNicsCalculated": true,
              |     "messages": true,
              |     "taxableIncome": true,
              |     "endOfYearEstimate": true,
              |     "allowancesDeductionsAndReliefs": true
              |    }
              |  }
              |}""".stripMargin).as[JsObject]

        val successOutput: JsObject = Json.parse(
          s"""{
             |    "id": "$calcId",
             |    "taxYear": "2018-19",
             |    "requestedBy": "customer",
             |    "calculationReason": "customerRequest",
             |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
             |    "calculationType": "crystallisation",
             |    "intentToCrystallise": true,
             |    "crystallised": false
             |}""".stripMargin).as[JsObject]

        val hateoas: JsObject = Json.parse(
          s"""{
             |    "links": [
             |      {
             |       "href": "/individuals/calculations/$nino/self-assessment/$calcId",
             |       "method": "GET",
             |       "rel": "self"
             |      },
             |      {
             |       "href": "/individuals/calculations/$nino/self-assessment/$calcId/income-tax-nics-calculated",
             |       "method": "GET",
             |       "rel": "income-tax-and-nics-calculated"
             |      },
             |      {
             |       "href": "/individuals/calculations/$nino/self-assessment/$calcId/taxable-income",
             |       "method": "GET",
             |       "rel": "taxable-income"
             |      },
             |      {
             |       "href": "/individuals/calculations/$nino/self-assessment/$calcId/allowances-deductions-reliefs",
             |       "method": "GET",
             |       "rel": "allowances-deductions-reliefs"
             |      },
             |      {
             |       "href": "/individuals/calculations/$nino/self-assessment/$calcId/end-of-year-estimate",
             |       "method": "GET",
             |       "rel": "end-of-year-estimate"
             |      },
             |      {
             |       "href": "/individuals/calculations/$nino/self-assessment/$calcId/messages",
             |       "method": "GET",
             |       "rel": "messages"
             |      }
             |    ]
             |}""".stripMargin).as[JsObject]

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successOutput.deepMerge(hateoas)
      }

      "valid request is made and no error messages and one metadata returned" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val successBody: JsObject = Json.parse(s"""|{
          |  "metadata": {
          |    "id": "$calcId",
          |    "taxYear": "2018-19",
          |    "requestedBy": "customer",
          |    "calculationReason": "customerRequest",
          |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
          |    "calculationType": "crystallisation",
          |    "intentToCrystallise": true,
          |    "crystallised": false,
          |    "metadataExistence": {
          |     "incomeTaxAndNicsCalculated": true,
          |     "messages": false,
          |     "taxableIncome": false,
          |     "endOfYearEstimate": false,
          |     "allowancesDeductionsAndReliefs": false
          |    }
          |  }
          |}""".stripMargin).as[JsObject]


        val successOutput: JsObject = Json.parse(s"""{
          |    "id": "$calcId",
          |    "taxYear": "2018-19",
          |    "requestedBy": "customer",
          |    "calculationReason": "customerRequest",
          |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
          |    "calculationType": "crystallisation",
          |    "intentToCrystallise": true,
          |    "crystallised": false
          |}""".stripMargin).as[JsObject]

        val hateoas: JsObject = Json.parse(s"""{
            |    "links": [
            |      {
            |       "href": "/individuals/calculations/$nino/self-assessment/$calcId",
            |       "method": "GET",
            |       "rel": "self"
            |      },
            |      {
            |       "href": "/individuals/calculations/$nino/self-assessment/$calcId/income-tax-nics-calculated",
            |       "method": "GET",
            |       "rel": "income-tax-and-nics-calculated"
            |      }
            |    ]
            |}""".stripMargin).as[JsObject]

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successOutput.deepMerge(hateoas)
      }

      "valid request is made and error messages are returned" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val successBody: JsObject = Json.parse(s"""|{
            |  "metadata": {
            |    "id": "$calcId",
            |    "taxYear": "2018-19",
            |    "requestedBy": "customer",
            |    "calculationReason": "customerRequest",
            |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
            |    "calculationType": "crystallisation",
            |    "intentToCrystallise": true,
            |    "crystallised": false,
            |    "calculationErrorCount" : 1,
            |     "metadataExistence": {
            |       "incomeTaxAndNicsCalculated": false,
            |       "messages": false,
            |       "taxableIncome": false,
            |       "endOfYearEstimate": false,
            |       "allowancesDeductionsAndReliefs": false
            |    }
            |  }
            |}""".stripMargin).as[JsObject]

        val successOutput: JsObject = Json.parse(s"""{
            |    "id": "$calcId",
            |    "taxYear": "2018-19",
            |    "requestedBy": "customer",
            |    "calculationReason": "customerRequest",
            |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
            |    "calculationType": "crystallisation",
            |    "intentToCrystallise": true,
            |    "crystallised": false,
            |    "calculationErrorCount" : 1
            |}""".stripMargin).as[JsObject]

        val hateoasErrors: JsObject = Json.parse(s"""{
            |    "links": [
            |      {
            |       "href": "/individuals/calculations/$nino/self-assessment/$calcId",
            |       "method": "GET",
            |       "rel": "self"
            |      }
            |    ]
            |}""".stripMargin).as[JsObject]

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successOutput.deepMerge(hateoasErrors)
      }

      "valid request is made and error messages are returned, and messages metadata is returned" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val successBody: JsObject = Json.parse(s"""|{
                                                  |  "metadata": {
                                                  |    "id": "$calcId",
                                                  |    "taxYear": "2018-19",
                                                  |    "requestedBy": "customer",
                                                  |    "calculationReason": "customerRequest",
                                                  |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
                                                  |    "calculationType": "crystallisation",
                                                  |    "intentToCrystallise": true,
                                                  |    "crystallised": false,
                                                  |    "calculationErrorCount" : 1,
                                                  |     "metadataExistence": {
                                                  |       "incomeTaxAndNicsCalculated": false,
                                                  |       "messages": true,
                                                  |       "taxableIncome": false,
                                                  |       "endOfYearEstimate": false,
                                                  |       "allowancesDeductionsAndReliefs": false
                                                  |    }
                                                  |  }
                                                  |}""".stripMargin).as[JsObject]

        val successOutput: JsObject = Json.parse(s"""{
                                                  |    "id": "$calcId",
                                                  |    "taxYear": "2018-19",
                                                  |    "requestedBy": "customer",
                                                  |    "calculationReason": "customerRequest",
                                                  |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
                                                  |    "calculationType": "crystallisation",
                                                  |    "intentToCrystallise": true,
                                                  |    "crystallised": false,
                                                  |    "calculationErrorCount" : 1
                                                  |}""".stripMargin).as[JsObject]

        val hateoasErrors: JsObject = Json.parse(s"""{
                                                   |    "links": [
                                                   |      {
                                                   |       "href": "/individuals/calculations/$nino/self-assessment/$calcId",
                                                   |       "method": "GET",
                                                   |       "rel": "self"
                                                   |      },
                                                   |      {
                                                   |       "href": "/individuals/calculations/$nino/self-assessment/$calcId/messages",
                                                   |       "method": "GET",
                                                   |       "rel": "messages"
                                                   |      }
                                                   |    ]
                                                   |}""".stripMargin).as[JsObject]

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successOutput.deepMerge(hateoasErrors)
      }

    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, requestCalcId: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String   = requestNino
            override val calcId: String = requestCalcId

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request.get)
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          ("AA1123A", "12345678", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "AAAAAAA", BAD_REQUEST, CalculationIdFormatError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "backend service error" when {

        def errorBody(code: String): String =
          s"""{
             |  "code": "$code",
             |  "message": "backend message"
             |}""".stripMargin

        def serviceErrorTest(backendStatus: Int, backendCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $backendCode error and status $backendStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BackendStub.onError(BackendStub.GET, backendUrl, backendStatus, errorBody(backendCode))
            }

            val response: WSResponse = await(request.get)
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "FORMAT_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError),
          (NOT_FOUND, "MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
