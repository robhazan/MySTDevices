/**
 *  Parent_ST_Anything_Sump_Monitor.device.groovy
 */
 
metadata {
	definition (name: "Parent_ST_Anything_Sump_Monitor", namespace: "robhazan", author: "Rob Hazan") {
		capability "Configuration"
        capability "Refresh"
//        capability "Button"
//        capability "Holdable Button"

		attribute "contact1Low", "string"
		attribute "contact2Mid", "string"
		attribute "contact3Hi", "string"
	}

    simulator {
    }

    // Preferences
	preferences {
		input "ip", "text", title: "Arduino IP Address", description: "IP Address in form 192.168.1.226", required: true, displayDuringSetup: true
		input "port", "text", title: "Arduino Port", description: "port in form of 8090", required: true, displayDuringSetup: true
		input "mac", "text", title: "Arduino MAC Addr", description: "MAC Address in form of 02A1B2C3D4E5", required: true, displayDuringSetup: true
		input "numButtons", "number", title: "Number of Buttons", description: "Number of Buttons to be implemented", defaultValue: 0, required: true, displayDuringSetup: true
	}

	// Tile Definitions
	tiles (scale: 2){
		standardTile("contact1Low", "device.contact", width: 2, height: 2) {
		    state "open", label: "off", icon: "st.alarm.water.dry", backgroundColor: "#ffffff"
		    state "closed", label: "on", icon: "st.alarm.water.wet", backgroundColor: "#44b621"
		}
		
		standardTile("contact2Mid", "device.contact", width: 2, height: 2) {
		    state "open", label: "off", icon: "st.alarm.water.dry", backgroundColor: "#ffffff"
		    state "closed", label: "on", icon: "st.alarm.water.wet", backgroundColor: "#f1d801"
		}
		
		standardTile("contact3Hi", "device.contact", width: 2, height: 2) {
		    state "open", label: "off", icon: "st.alarm.water.dry", backgroundColor: "#ffffff"
		    state "closed", label: "on", icon: "st.alarm.water.wet", backgroundColor: "#bc2323"
		}
		
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
			state "default", label:'Refresh', action: "refresh.refresh", icon: "st.secondary.refresh-icon"
		}
        
		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
			state "configure", label:'Configure', action:"configuration.configure", icon:"st.secondary.tools"
		}

	}
}

// parse events into attributes
def parse(String description) {
	//log.debug "Parsing '${description}'"
	def msg = parseLanMessage(description)
	def headerString = msg.header

	if (!headerString) {
		//log.debug "headerstring was null for some reason :("
    }

	def bodyString = msg.body

	if (bodyString) {
        log.debug "Parsing: $bodyString"
    	def parts = bodyString.split(" ")
    	def name  = parts.length>0?parts[0].trim():null
    	def value = parts.length>1?parts[1].trim():null
        
		def nameparts = name.split("\\d+", 2)
		def namebase = nameparts.length>0?nameparts[0].trim():null
        def namenum = name.substring(namebase.length()).trim()
		
        def results = []
        
		if (name.startsWith("button")) {
			//log.debug "In parse:  name = ${name}, value = ${value}, btnName = ${name}, btnNum = ${namemun}"
        	results = createEvent([name: namebase, value: value, data: [buttonNumber: namenum], descriptionText: "${namebase} ${namenum} was ${value} ", isStateChange: true, displayed: true])
			log.debug results
		} else {
                results = createEvent(name: name, value: value)
                log.debug results
		}
		
		return results
	}
}

private getHostAddress() {
    def ip = settings.ip
    def port = settings.port
    
    log.debug "Using ip: ${ip} and port: ${port} for device: ${device.id}"
    return ip + ":" + port
}

def sendEthernet(message) {
	log.debug "Executing 'sendEthernet' ${message}"
	if (settings.ip != null && settings.port != null) {
        sendHubCommand(new physicalgraph.device.HubAction(
            method: "POST",
            path: "/${message}?",
            headers: [ HOST: "${getHostAddress()}" ]
        ))
    }
    else {
        state.alertMessage = "Sump Monitor Device has not yet been fully configured. Click the 'Gear' icon, enter data for all fields, and click 'Done'"
        runIn(2, "sendAlert")   
    }
}

// handle commands
/*
def childAlarmOn(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childAlarmOn($dni), name = ${name}"
    sendEthernet("${name} both")
}

def childAlarmSiren(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childAlarmOn($dni), name = ${name}"
    sendEthernet("${name} siren")
}

def childAlarmStrobe(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childAlarmOn($dni), name = ${name}"
    sendEthernet("${name} strobe")
}

def childAlarmBoth(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childAlarmOn($dni), name = ${name}"
    sendEthernet("${name} both")
}

def childAlarmOff(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childAlarmOff($dni), name = ${name}"
    sendEthernet("${name} off")
}

def childAlarmTest(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childAlarmTest($dni), name = ${name}"
    sendEthernet("${name} both")
	runIn(3, childAlarmTestOff, [data: [devicenetworkid: dni]])
}

def childAlarmTestOff(data) {
	childAlarmOff(data.devicenetworkid)
}

void childDoorOpen(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childDoorOpen($dni), name = ${name}"
    sendEthernet("${name} on")
}

void childDoorClose(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childDoorClose($dni), name = ${name}"
    sendEthernet("${name} on")
}

void childOn(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childOn($dni), name = ${name}"
    sendEthernet("${name} on")
}

void childOff(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childOff($dni), name = ${name}"
    sendEthernet("${name} off")
}

void childSetLevel(String dni, value) {
    def name = dni.split("-")[-1]
    log.debug "childSetLevel($dni), name = ${name}, level = ${value}"
    sendEthernet("${name} ${value}")
}

void childRelayOn(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childRelayOn($dni), name = ${name}"
    sendEthernet("${name} on")
}

void childRelayOff(String dni) {
    def name = dni.split("-")[-1]
    log.debug "childRelayOff($dni), name = ${name}"
    sendEthernet("${name} off")
}*/

def configure() {
	log.debug "Executing 'configure()'"
    updateDeviceNetworkID()
	sendEvent(name: "numberOfButtons", value: numButtons)
}

def refresh() {
	log.debug "Executing 'refresh()'"
	sendEthernet("refresh")
	sendEvent(name: "numberOfButtons", value: numButtons)

	//debugging
	sendEvent(name:"contact1Low", value:"closed")
	sendEvent(name:"contact2Mid", value:"open")
	sendEvent(name:"contact3Hi", value:"open")
}

def installed() {
	log.debug "Executing 'installed()'"
    if ( device.deviceNetworkId =~ /^[A-Z0-9]{12}$/)
    {
    }
    else
    {
        state.alertMessage = "Sump Monitor Device has not yet been fully configured. Click the 'Gear' icon, enter data for all fields, and click 'Done'"
        runIn(2, "sendAlert")
    }
}

def initialize() {
	log.debug "Executing 'initialize()'"
    sendEvent(name: "numberOfButtons", value: numButtons)
}

def updated() {
	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 5000) {
		state.updatedLastRanAt = now()
		log.debug "Executing 'updated()'"
    	runIn(3, "updateDeviceNetworkID")
		sendEvent(name: "numberOfButtons", value: numButtons)
	}
	else {
//		log.trace "updated(): Ran within last 5 seconds so aborting."
	}
}

def updateDeviceNetworkID() {
	log.debug "Executing 'updateDeviceNetworkID'"
    if(device.deviceNetworkId!=mac) {
    	log.debug "setting deviceNetworkID = ${mac}"
        device.setDeviceNetworkId("${mac}")
	}
    //Need deviceNetworkID updated BEFORE we can create Child Devices
	//Have the Arduino send an updated value for every device attached.  This will auto-created child devices!
	refresh()
}

private sendAlert() {
   sendEvent(
      descriptionText: state.alertMessage,
	  eventType: "ALERT",
	  name: "deviceConfigured",
	  value: "false",
	  displayed: true,
   )
}

private boolean containsDigit(String s) {
    boolean containsDigit = false;

    if (s != null && !s.isEmpty()) {
//		log.debug "containsDigit .matches = ${s.matches(".*\\d+.*")}"
		containsDigit = s.matches(".*\\d+.*")
    }
    return containsDigit
}
