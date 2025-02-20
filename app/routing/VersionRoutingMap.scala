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

package routing

import com.google.inject.ImplementedBy
import config.{AppConfig, FeatureSwitch}
import definition.Versions.{VERSION_1, VERSION_2}
import javax.inject.Inject
import play.api.routing.Router
import utils.Logging

// So that we can have API-independent implementations of
// VersionRoutingRequestHandler and VersionRoutingRequestHandlerSpec
// implement this for the specific API...
@ImplementedBy(classOf[VersionRoutingMapImpl])
trait VersionRoutingMap extends Logging {
  val defaultRouter: Router

  val map: Map[String, Router]

  final def versionRouter(version: String): Option[Router] = map.get(version)
}

// Add routes corresponding to available versions...
case class VersionRoutingMapImpl @Inject()(appConfig: AppConfig,
                                           defaultRouter: Router,
                                           v1Router: v1.Routes,
                                           v1r2Router: v1r2.Routes,
                                           v2Router: v2.Routes,
                                           liveRouter: live.Routes) extends VersionRoutingMap {

  val featureSwitch: FeatureSwitch = FeatureSwitch(appConfig.featureSwitch)

  val map: Map[String, Router] = Map(
    VERSION_1 -> {
      if (featureSwitch.isV1R2RoutingEnabled) {
        v1r2Router
      }
      else if (featureSwitch.isFullRoutingEnabled) {
        v1Router
      }
      else {
        liveRouter
      }
    },
    VERSION_2 -> v2Router
  )
}