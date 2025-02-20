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

package v1.connectors

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import config.AppConfig

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MtdIdLookupConnector @Inject()(http: HttpClient,
                                     appConfig: AppConfig) {

  def getMtdId(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[MtdIdLookupOutcome] = {
    import v1.connectors.httpparsers.MtdIdLookupHttpParser.mtdIdLookupHttpReads

    http.GET[MtdIdLookupOutcome](s"${appConfig.mtdIdBaseUrl}/mtd-identifier-lookup/nino/$nino", queryParams = Seq.empty)
  }
}
