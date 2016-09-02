/**
 *  Flex Connect
 *
 *  Copyright 2016 Ross Hambrick
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Flex Connect",
    namespace: "rosshambrick",
    author: "Ross Hambrick",
    description: "A flexible way to connect any sensor, switch, etc to trigger any device, event, status, etc.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("IF") {
        input "ifMotion", "capability.motionSensor", title: "Motion detected", required: false, multiple: true
        input "ifContactOpened", "capability.contactSensor", title: "Contact opened", required: false, multiple: true
        input "ifContactClosed", "capability.contactSensor", title: "Contact closed", required: false, multiple: true
    }
    section("THEN") {
        input "thenSwitchOn", "capability.switch", title: "Turn on", required: false, multiple: true
        input "thenSwitchOff", "capability.switch", title: "Turn off", required: false, multiple: true
        input "thenThermostat", "capability.thermostat", title: "Change temperature", required: false, submitOnChange: true
        if (thenThermostat)	input "coolSetPoint", "decimal", title: "Cool Set Point", required: false 
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(ifMotion, "motion.active", motionDetectedHandler)
}

def motionDetectedHandler(evt) {
    log.debug "motionDetectedHandler called: $evt"
    if (thenSwitchOn)  thenSwitchOn.on()
    if (thenSwitchOff) thenSwitchOff.off()
}
