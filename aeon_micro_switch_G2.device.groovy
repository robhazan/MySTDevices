metadata {
	// Automatically generated. Make future change here.
	definition (name: "My Aeon Metering Switch v2", namespace: "jscgs350", author: "SmartThings") {
		capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
        
		fingerprint inClusters: "0x25,0x32"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
            tileAttribute ("statusText", key: "SECONDARY_CONTROL") {
           		attributeState "statusText", label:'${currentValue}'       		
            }
		}
		standardTile("configure", "device.power", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
		standardTile("refresh", "device.power", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        valueTile("statusText", "statusText", inactiveLabel: false, width: 2, height: 2) {
			state "statusText", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		main "switch"
		details(["refresh","configure"])
	}
}

def parse(String description) {
	def result = null
	def cmd = zwave.parse(description, [0x20: 1, 0x32: 1])
	if (cmd) {
		result = createEvent(zwaveEvent(cmd))
	}

	return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
	[
		name: "switch", value: cmd.value ? "on" : "off", type: "physical"
	]
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd)
{
	[
		name: "switch", value: cmd.value ? "on" : "off", type: "digital"
	]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
	[:]
}

def on() {
	delayBetween([
			zwave.basicV1.basicSet(value: 0xFF).format(),
			zwave.switchBinaryV1.switchBinaryGet().format()
	])
}

def off() {
	delayBetween([
			zwave.basicV1.basicSet(value: 0x00).format(),
			zwave.switchBinaryV1.switchBinaryGet().format()
	])
}

def poll() {
    refresh()
}

def refresh() {
    log.debug "${device.name} refresh"
		zwave.switchBinaryV1.switchBinaryGet().format()
}

def configure() {
    log.debug "${device.name} configure"
	delayBetween([
    zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: 0).format(),      // Disable selective reporting, so always update based on schedule below <set to 1 to reduce network traffic>
    zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: 50).format(),     // (DISABLED by first option) Don't send unless watts have changed by 50 <default>
    zwave.configurationV1.configurationSet(parameterNumber: 8, size: 1, scaledConfigurationValue: 10).format(),     // (DISABLED by first option) Or by 10% <default>
    zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 4).format(),   // Combined energy in Watts
    zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: 15).format(),   // Every 15 Seconds (for Watts)
    zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 8).format(),    // Combined energy in kWh
    zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: 60).format(),  // every 60 seconds (for kWh)
    zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 0).format(),    // Disable report 3
    zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: 0).format()   // Disable report 3
	])
}
