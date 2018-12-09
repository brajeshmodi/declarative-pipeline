/**
 * Copyright Javatar LLC 2018 ©
 * Licensed under the License located in the root of this repository (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://github.com/JavatarPro/pipeline-utils/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.javatar.pipeline.service.webhook;

import static pro.javatar.pipeline.service.PipelineDslHolder.dsl

class WebhookReceiverServiceImpl implements WebhookReceiverService, Serializable {

    @Override
    public String getPreviousReleaseVersion(def serviceName) {
        //todo: read from yaml
        def webHookReceiverUrl = "https://webhook-receiver/api/previous-release-version/${serviceName}";
        dsl.echo "Retrieve last release version via ${webHookReceiverUrl}"

        def post = new URL(webHookReceiverUrl).openConnection();
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        post.setRequestProperty("Content-Type", "application/json")

        def resp = post.getResponseCode();
        dsl.echo "Response status code is ${resp}"

        if(resp.equals(200)) {
            def json = post.getInputStream().getText();
            dsl.echo "Previous release version is ${json.version}"
            return json.version;
        }

        return null;
    }
}
