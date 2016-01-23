/**
 *  WeMo Maker Garage Door via IFTTT
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
	name: "WeMo Maker Garage Door via IFTTT", 
    namespace: "rosshambrick",
    author: "Ross Hambrick",
    description: "This app will allow you to hook up a WeMo Maker enabled garage door into SmartThings",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("IFTTT Event Sources"){
		input "opener", "capability.switch", title: "IFTTT Garage Door Opener?", required: true
		input "sensor", "capability.switch", title: "IFTTT Garage Door Sensor?", required: true
		input "door", "capability.garageDoorControl", title: "Simulated Garage Door?", required: true
    }

//	section("Timeout before checking if the door opened or closed correctly?"){
//		input "checkTimeout", "number", title: "Door Operation Check Timeout?", required: true, defaultValue: 15
//	}

	section("Notifications") {
		input("recipients", "contact", title: "Send notifications to") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phone1", "phone", title: "Send a Text Message?", required: false
		}
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
	subscribe(sensor, "switch", sensorHandler)
   	subscribe(door, "door", doorHandler)

 	if ((sensor.currentSwitch == "on") != (door.currentDoor == "closed")) {
		log.debug "States are out of sync"

        if (sensor.currentSwitch == "off") {
            log.debug "Setting simulated door to OPEN"
            door.open()
		} else {
            log.debug "Setting simulated door to CLOSED"
            door.close()
		}
	}
}

def sensorHandler(evt) {
	log.debug "sensorHandler(${evt.value})"
    
    if (evt.value == "on" && door.currentDoor != "closed") {
		log.debug "Closing the simulated door"
		door.close()
    } else if (evt.value == "off" && door.currentDoor != "open") {
		log.debug "Opening the simulated door"
        door.open()
    } else {
    	log.debug "Nothing to do"
    }
}

def doorHandler(evt) {
	log.debug "doorHandler(${evt.value})"
        
    if (evt.value == "opening" && sensor.currentSwitch == "on") {
		log.debug "Opening the real door"
        triggerOpener()
    } else if (evt.value == "closing" && sensor.currentSwitch == "off") {
        log.debug "Closing the real door"
        triggerOpener()
    } else if (evt.value == "open") {
    	log.debug "Turning sensor off"
        sensor.off()
    } else if (evt.value == "closed") {
    	log.debug "Turning sensor on"
        sensor.on()
    } else {
    	log.debug "Nothing to do"
    }
}

//def doorContactHandler(evt) {
//	log.debug "doorContactHandler(${evt.value})"
//    //this duplicated the open so I removed it
//}

def triggerOpener() {
    if (opener.currentSwitch == "off") {
        opener.on()
    } else {
        opener.off()
    }
}

// private mysend(msg) {
//   if (location.contactBookEnabled) {
//     log.debug("sending notifications to: ${recipients?.size()}")
//     sendNotificationToContacts(msg, recipients)
//   } else {
//     if (sendPushMessage != "No") {
//       log.debug("sending push message")
//       sendPush(msg)
//     }
//     if (phone1) {
//       log.debug("sending text message")
//       sendSms(phone1, msg)
//     }
//   }
// 
//   log.debug msg
// }

def checkIfActuallyClosed() {
  def sensorState = sensor.currentSwitch
  def doorState = door.currentDoor
 
  if (sensorState == "off" && doorState == "closed") {
   	log.debug "Didn't close as expected"
    door.open()
  }
}

def checkIfActuallyOpened() {
  def sensorState = sensor.currentSwitch
  def doorState = door.currentDoor

  if (sensorState == "on" && doorState == "open") {
	log.debug "Didn't open as expected"
	door.close()
  }
}
