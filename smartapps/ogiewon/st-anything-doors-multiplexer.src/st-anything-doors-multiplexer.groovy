/**
 *  ST_Anything Doors Multiplexer - ST_Anything_Doors_Multiplexer.smartapp.groovy
 *
 *  Copyright 2015 Daniel Ogorchock
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
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2015-01-10  Dan Ogorchock  Original Creation
 *    2015-01-11  Dan Ogorchock  Reduced unnecessary chatter to the virtual devices
 *    2015-01-18  Dan Ogorchock  Added support for Virtual Temperature/Humidity Device
 *
 */
 
definition(
    name: "ST_Anything Doors Multiplexer",
    namespace: "ogiewon",
    author: "Daniel Ogorchock",
    description: "Connects single Arduino with multiple DoorControl and ContactSensor devices to their virtual device counterparts.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
/*	section("Select the Garage Doors (Virtual Door Control devices)") {
		input "leftdoor", title: "Left Garage Door", "capability.doorControl"
		input "rightdoor", title: "Right Garage Door", "capability.doorControl"
	}
*/
	section("Select door sensors") {
		input "contactFrontPatio", title: "Virtual Contact Sensor for Front & Patio Doors", "capability.contactSensor"
		input "contactGarageMudroom", title: "Virtual Contact Sensor for Garage & Mudroom Doors", "capability.contactSensor"
	}
    
    section("Select motion sensors") {
		input "motionFront", title: "Virtual Motion Sensor for Front Sensors", "capability.motionSensor"
		input "motionBack", title: "Virtual Motion Sensor for Back Sensor", "capability.motionSensor"
	}

/*
	section("Select the Virtual Temperature/Humidity devices") {
		input "temphumid_1", title: "1st Temp-Humidity Sensor", "capability.temperatureMeasurement", required: false
	}
*/
	section("Select the Arduino ST_Anything_Doors device") {
		input "arduino", "capability.contactSensor"
    }    
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribe()
}

def subscribe() {
    
    subscribe(arduino, "contactGarageMudroom.open", contactGarageMudroomOpen)
    subscribe(arduino, "contactGarageMudroom.closed", contactGarageMudroomClosed)
    
    subscribe(arduino, "contactFrontPatio.open", contactFrontPatioOpen)
    subscribe(arduino, "contactFrontPatio.closed", contactFrontPatioClosed)

    subscribe(arduino, "motionFront.active", motionFrontActive)
    subscribe(arduino, "motionFront.inactive", motionFrontInactive)
    
    subscribe(arduino, "motionBack.active", motionBackActive)
    subscribe(arduino, "motionBack.inactive", motionBackInactive)

}

/*
// --- Left Garage Door --- 
def leftDoorOpen(evt)
{
    if (leftdoor.currentValue("contact") != "open") {
    	log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	leftdoor.open()
	}
}

def leftDoorOpening(evt)
{
    if (leftdoor.currentValue("contact") != "opening") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	leftdoor.opening()
	}    
}

def leftDoorClosing(evt)
{
    if (leftdoor.currentValue("contact") != "closing") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	leftdoor.closing()
	}
}

def leftDoorClosed(evt)
{
    if (leftdoor.currentValue("contact") != "closed") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	leftdoor.close()
	}
}    

def leftDoorPushButton(evt)
{
    log.debug "virtualGarageDoor($evt.name: $evt.value: $evt.deviceId)"
    arduino.pushLeft()
}

// --- Right Garage Door --- 
def rightDoorOpen(evt)
{
    if (rightdoor.currentValue("contact") != "open") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
		rightdoor.open()
	}
}

def rightDoorOpening(evt)
{
    if (rightdoor.currentValue("contact") != "opening") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	rightdoor.opening()
	}    
}

def rightDoorClosing(evt)
{
    if (rightdoor.currentValue("contact") != "closing") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	rightdoor.closing()
	}
}

def rightDoorClosed(evt)
{
    if (rightdoor.currentValue("contact") != "closed") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	rightdoor.close()
	}
}

def rightDoorPushButton(evt)
{
    log.debug "virtualGarageDoor($evt.name: $evt.value: $evt.deviceId)"
    arduino.pushRight()
}
*/

// --- contactGarageMudroom --- 
def contactGarageMudroomOpen(evt)
{
    if (contactGarageMudroom.currentValue("contact") != "open") {
    	log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	contactGarageMudroom.openme()
    }
}

def contactGarageMudroomClosed(evt)
{
    if (contactGarageMudroom.currentValue("contact") != "closed") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	contactGarageMudroom.closeme()
    }
}

// --- contactFrontPatio --- 
def contactFrontPatioOpen(evt)
{
    if (contactFrontPatio.currentValue("contact") != "open") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	contactFrontPatio.openme()
    }
}

def contactFrontPatioClosed(evt)
{
    if (contactFrontPatio.currentValue("contact") != "closed") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	contactFrontPatio.closeme()
	}
}

// --- Front Motion Sensor --- 
def motionFrontActive(evt)
{
    if (motionFront.currentValue("motion") != "active") {
    	log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	motionFront.active()
    }
}

def motionFrontInactive(evt)
{
    if (motionFront.currentValue("motion") != "inactive") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	motionFront.inactive()
    }
}

// --- Back Motion Sensor --- 
def motionBackActive(evt)
{
    if (motionBack.currentValue("motion") != "active") {
    	log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	motionBack.active()
    }
}

def motionBackInactive(evt)
{
    if (motionBack.currentValue("motion") != "inactive") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	motionBack.inactive()
    }
}

/*
// --- Kitchen Door --- 
def kitchenDoorOpen(evt)
{
    if (kitchendoor.currentValue("contact") != "open") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	kitchendoor.openme()
	}
}

def kitchenDoorClosed(evt)
{
    if (kitchendoor.currentValue("contact") != "closed") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	kitchendoor.closeme()
	}
}


// --- Garage Side Door --- 
def garagesideDoorOpen(evt)
{
    if (garagesidedoor.currentValue("contact") != "open") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
 	   garagesidedoor.openme()
	}
}

def garagesideDoorClosed(evt)
{
    if (garagesidedoor.currentValue("contact") != "closed") {
	    log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
	    garagesidedoor.closeme()
	}
}

// --- Temperature/Humidity ---
def temphumid_1_UpdateTemp(evt)
{
    log.debug "temperature: $evt.value, $evt"
    temphumid_1.updateTemperature(evt.value)
}

def temphumid_1_UpdateHumid(evt)
{
    log.debug "humidity: $evt.value, $evt"
    temphumid_1.updateHumidity(evt.value)
}
*/

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}