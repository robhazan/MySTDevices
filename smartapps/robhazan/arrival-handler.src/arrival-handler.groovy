/**
*
* Rob's Custom Arrival Handler
*
*/
definition(
    name: "Arrival Handler",
    namespace: "robhazan",
    author: "Rob Hazan",
    description: "V1: Set thermostats upon arrival, then switch to designated mode. ",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	 page(name: "setThermostat")
}

def setThermostat() {
	dynamicPage(name: "setThermostat", title: null, install: true, uninstall: true) {
 
		section{
			input(name: "thermostats", type: "capability.thermostat", title: "Thermostats",
            	description: null, multiple: true, required: true)
        }
        
	    section{
    	    input(name: "state", type: "enum", title: "State", 
            	description: null, multiple: false, required: true, options: ["Set","Resume"], submitOnChange: true)
	    }
        
        section {
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: true
            input "newMode", "mode", title: "Change to mode upon completion", required: false
            
        }        
        
		if (settings.state == "Set") {
			section {
				input(name: "heatingSetpoint", type: "number", title: "Heating Setpoint Degrees?", required: true)
            }
            section {
				input(name: "coolingSetpoint", type: "number", title: "Cooling Setpoint Degrees?", required: true) 
			}
		}
	}
}



def installed()
{
	subscribe(thermostats, "heatingSetpoint", heatingSetpointHandler)
	subscribe(thermostats, "coolingSetpoint", coolingSetpointHandler)
	subscribe(thermostats, "temperature", temperatureHandler)
	subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
}

def updated()
{
	unsubscribe()
	subscribe(thermostats, "heatingSetpoint", heatingSetpointHandler)
	subscribe(thermostats, "coolingSetpoint", coolingSetpointHandler)
	subscribe(thermostats, "temperature", temperatureHandler)
	subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
}

def heatingSetpointHandler(evt)
{
	log.debug "heatingSetpointHandler: $evt, $settings"
}

def coolingSetpointHandler(evt)
{
	log.debug "coolingSetpointHandler: $evt, $settings"
}

def temperatureHandler(evt)
{
	log.debug "TemperatureHandler: $evt, $settings"
}

def changedLocationMode(evt)
{
    if(settings.state == "Set")
    {
	  thermostats.setHeatingSetpoint(heatingSetpoint)
	  thermostats.setCoolingSetpoint(coolingSetpoint)
    }
    else if (settings.state == "Resume") 
    {
      thermostats.resumeProgram()
    }
    
    setLocationMode(newMode)
}

def appTouch(evt)
{
	log.debug "appTouch: $evt, $settings"
    log.debug "Current state: $settings.state"
    if(settings.state == "Set"){
       settings.state = "Resume"
       log.debug "setting state to Reset: $settings.state"
    }
    else {
       settings.state = "Set"
       log.debug "setting state to Set: $settings.state"
    }
  

}

// catchall
def event(evt)
{
	log.debug "value: $evt.value, event: $evt, settings: $settings, handlerName: ${evt.handlerName}"
}