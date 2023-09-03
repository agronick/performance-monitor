/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package org.prowl.torque.remote;
public interface ITorqueService extends android.os.IInterface
{
  /** Default implementation for ITorqueService. */
  class Default implements ITorqueService
  {
    /** Get the API version. The version will increment each time the API is revised. */
    @Override public int getVersion() throws android.os.RemoteException
    {
      return 0;
    }
    /**
     * Get the most recent value stored for the given PID.  This will return immediately whether or not data exists.
     * @param triggersDataRefresh Cause the data to be re-requested from the ECU
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public float getValueForPid(long pid, boolean triggersDataRefresh) throws android.os.RemoteException
    {
      return 0.0f;
    }
    /**
     * Get a textual, long description regarding the PID, already translated (when translation is implemented)
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public String getDescriptionForPid(long pid) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Get the shortname of the PID
     * @note If the PID returns multiple values, then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public String getShortNameForPid(long pid) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Get the Si unit in string form for the PID, if no Si unit is available, a textual-description is returned instead.
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public String getUnitForPid(long pid) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Get the minimum value expected for this PID
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public float getMinValueForPid(long pid) throws android.os.RemoteException
    {
      return 0.0f;
    }
    /**
     * Get the maximum value expected for this PId
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public float getMaxValueForPid(long pid) throws android.os.RemoteException
    {
      return 0.0f;
    }
    /**
     * Returns a list of currently 'active' PIDs. This list will change often. Try not to call this method too frequently.
     * 
     * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public long[] getListOfActivePids() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Returns a list of PIDs that have been reported by the ECU as supported.
     * 
     * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public long[] getListOfECUSupportedPids() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Returns a list of all the known available sensors, active or inactive.
     * 
     * Try not to call this method too frequently.
     * 
     * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     * @deprecated  See getPIDInformation(...), getPIDValues(...)  and listAllPIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public long[] getListOfAllPids() throws android.os.RemoteException
    {
      return null;
    }
    /** True if the user has granted full permissions to the plugin (to send anything that it wants) */
    @Override public boolean hasFullPermissions() throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Send a specific request over the OBD bus and return the response as a string
     * 
     * This is currently limited by the 'Allow full permissions' option in the settings.
     * 
     * Modes 01,02,03,07,09,0a,21,22 and 23 are permitted. Permission needs to be granted in the
     * app settings for any other modes.
     */
    @Override public String[] sendCommandGetResponse(String header, String command) throws android.os.RemoteException
    {
      return null;
    }
    /** Given this unit, get the users preferred units */
    @Override public String getPreferredUnit(String unit) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Add / Update a PID from your plugin to the main app, Your PID is keyed by name.
     * 
     * @deprecated - (see below)superceded by setPIDInformation(String name, String shortName, String unit, float max, float min, float value, String stringValue);
     */
    @Override public boolean setPIDData(String name, String shortName, String unit, float max, float min, float value) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Get connection state to ECU
     * @return true if connected to the ECU, false if the app has not yet started retrieving data from the ECU
     */
    @Override public boolean isConnectedToECU() throws android.os.RemoteException
    {
      return false;
    }
    /** Turn on or off test mode. This is for debugging only and simulates readings to some of the sensors. */
    @Override public boolean setDebugTestMode(boolean activateTestMode) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Returns a string array containing the vehicle profile information:
     * 
     *  Array:
     *     [0] Profile name
     *     [1] Engine Displacement (L)
     *     [2] Weight in kilogrammes
     *     [3] Fuel type (0 = Petrol, 1 = Diesel, 2 = E85 (Ethanol/Petrol)    *     [4] Boost adjustment
     *     [5] Max RPM setting
     *     [6] Volumetric efficiency (%)
     *     [7] Accumulated distance travelled
     */
    @Override public String[] getVehicleProfileInformation() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Store some information into the vehicle profile.
     * 
     * @param key  Prefix the 'key' with your apps classpath (to avoid conflicts). eg: "com.company.pluginname.SOME_KEY_NAME" is nice and clear  (Don't use any characters other than A-Za-z0-9 . and _)
     * @param The value to store.
     * @param saveToFile Set this to true (on your last 'set') to commit the information to disk.
     * @return 0 if successful.
     */
    @Override public int storeInProfile(String key, String value, boolean saveToFileNow) throws android.os.RemoteException
    {
      return 0;
    }
    /**
     * Retrieve some information from the vehicle profile.
     * 
     * @param Prefix the 'key' with your apps classpath (to avoid conflicts).
     */
    @Override public String retrieveProfileData(String key) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Retrieve the number of errors that Torque has seen happen at the adapter
     * 
     * Generally speaking, if this is above 0, then you have problems at the adapter side.
     */
    @Override public int getDataErrorCount() throws android.os.RemoteException
    {
      return 0;
    }
    /**
     * Get max PID read speed in PIDs read per second
     * 
     * This is reset each connection to the adpater.
     */
    @Override public double getPIDReadSpeed() throws android.os.RemoteException
    {
      return 0.0d;
    }
    /**
     * Gets the configured communications speed
     * 
     *  1 == slowest (for problematic adapters)
     *  2 == standard (The app default)
     *  3 == fast mode (selectable in the OBD2 adapter settings)
     * 
     * Fast mode provides a significant speed increase on some vehicles for the read speed of sensor information
     */
    @Override public int getConfiguredSpeed() throws android.os.RemoteException
    {
      return 0;
    }
    /** returns true if the user is currently logging data to the file */
    @Override public boolean isFileLoggingEnabled() throws android.os.RemoteException
    {
      return false;
    }
    /** returns true if the user is currently logging data to web */
    @Override public boolean isWebLoggingEnabled() throws android.os.RemoteException
    {
      return false;
    }
    /** returns the number of items that are configured to be logged to file. The more items configured to log (and with logging enabled), the slower the refresh rate of individual PIDs. */
    @Override public int getNumberOfLoggedItems() throws android.os.RemoteException
    {
      return 0;
    }
    /**
     * Get the time that the PID was last updated.
     * 
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     */
    @Override public long getUpdateTimeForPID(long pid) throws android.os.RemoteException
    {
      return 0L;
    }
    /**
     * Returns the scale of the requested PID
     * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
     */
    @Override public float getScaleForPid(long pid) throws android.os.RemoteException
    {
      return 0.0f;
    }
    /** Translate a string that *might* be in translation file in the main Torque app. Do not call this method repeatedly for the same text! */
    @Override public String translate(String originalText) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Method to send PID data in raw form.
     *  Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header
     * @deprecated - see sendPIDDataV2 (which allows diagnostic headers to be set, or left as null)
     */
    @Override public boolean sendPIDData(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Add / Update a PID from your plugin to the main app, Your PID is keyed by name.  This can be used to set a value on a PID in Torque.
     * 
     * @param value       The value to be shown in the display
     * @param stringValue A string value to be shown in the display - overrides 'value' when not null - note(try not to use numeric values here as they won't be unit converted for you).  Set to null to revert to using the 'float value'
     */
    @Override public boolean setPIDInformation(String name, String shortName, String unit, float max, float min, float value, String stringValue) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Get a list of all PIDs in torque, active or inactive.
     * 
     * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
     * @note (don't call this too often as it is computationally expensive!)
     */
    @Override public String[] listAllPIDs() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * List all only the active PIDs
     * 
     * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
     * @note (don't call this too often as it is computationally expensive!)
     */
    @Override public String[] listActivePIDs() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * List all only the active PIDs
     * 
     * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
     * @note (don't call this too often as it is computationally expensive!)
     */
    @Override public String[] listECUSupportedPIDs() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Return the current value only for the PID (or PIDs if you pass more than one)
     * 
     * Use this method to frequently get the value of PID(s).  Be aware that the more PIDs you request, the slower the update speed (as the OBD2 adapter will require to update more PIDs and this is the choke-point)
     * This method is asynchronous updates via the adapter
     * 
     * @deprecated please use getPIDValuesAsDouble(...) instead
     */
    @Override public float[] getPIDValues(String[] pidsToRetrieve) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Return all the PID information in a single transaction. This is the new preferred method to get data from PIDs
     * 
     * The unit returned is the raw unit (used for getPIDValues) - you can get the users preferred unit by using the relevant API call
     * 
     * Format of returned string array:
     *  String[] {
     *       String "pid information in csv format"
     *       String "pid information in csv format"
     *       ...(etc)
     *    }
     * 
     *  Example:
     * 
     *   String[] {
     *      "<longName>,<shortName>,<unit>,<maxValue>,<minValue>,<scale>",
     *      "Intake Manifold Pressure,Intake,kPa,255,0,1",
     *      "Accelerator Pedal Position E,Accel(E),%,100,0,1"
     *   }
     */
    @Override public String[] getPIDInformation(String[] pidIDs) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Get the time that the PID was last updated.
     * 
     * @returns time when the PID value was retrieved via OBD/etc in milliseconds
     */
    @Override public long[] getPIDUpdateTime(String[] pidIDs) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Retrieves a list of PID values
     * 
     * @deprecated See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
     */
    @Override public float[] getValueForPids(long[] pids) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Method to send PID data in raw form - this is for when pid plugin vendors want to protect their hard work when they have decyphed or licensed PIDs
     * Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header
     * @deprecated - see sendPIDDataPrivateV2 (which allows diagnostic headers to be set, or left as null)
     */
    @Override public boolean sendPIDDataPrivate(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Cause Torque to stop communicating with the adpter so external plugins can take full control
     * 
     * This is when you want to do special things like reconfigure the adpater to talk to special units (ABS, SRS, Body control, etc)
     * which may require torque to stay away from asking information from the adpater
     * 
     * @return an int depicting the state of the lock
     * 
     *   0 = Lock gained OK - you now have exclusive access
     *  -1 = Lock failed due to unknown reason
     *  -2 = Lock failed due to another lock already being present (from your, or another plugin)
     *  -3 = Lock failed due to not being connected to adapter
     *  -4 = reserved - you still didn't get a lock.
     *  -5 = No permissions to access in this manner (enable the ticky-box in the plugin settings for 'full access')
     *  -6 = Lock failed due to timeout trying to get a lock (10 second limit hit)
     */
    @Override public int requestExclusiveLock(String pluginName) throws android.os.RemoteException
    {
      return 0;
    }
    /**
     * Release the exclusive lock the plugin has with the adapter.
     * 
     * @param torqueMustReInitializeTheAdapter set this to TRUE if torque must reinit comms with the vehicle ECU to continue communicating - if it is set to FALSE, then torque will simply continue trying to talk to the ECU. Take special care to ensure the adpater is in the proper state to do this
     */
    @Override public boolean releaseExclusiveLock(String pluginName, boolean torqueMustReInitializeTheAdapter) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Method to send PID data in raw form.
     * This updated method allows diagnostic headers to be set if required (for special commands) or left as null.
     *  Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header,Start diagnostic command, Stop diagnostic command
     */
    @Override public boolean sendPIDDataV2(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header, String[] startDiagnostic, String[] stopDiagnostic) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Method to send PID data in raw form - this is for when pid plugin vendors want to protect their hard work when they have decyphed or licensed PIDs
     * This updated method allows diagnostic headers to be set if required (for special commands) or left as null.
     * Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header, Start diagnostic command, Stop diagnostic command
     */
    @Override public boolean sendPIDDataPrivateV2(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header, String[] startDiagnostic, String[] stopDiagnostic) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * This retrieves the last PID that torque read from the adapter in RAW form (as the adapter sent it) 'NOT READY' may be returned
     * if the adapter has not yet retrieved that PID. The OBDCommand should be as sent to the adpater (no spaces - eg: '010D')
     * 
     * Despite the name, this cannot be used to send commands directly to the adapter, use the sendCommandGetResponse(...) method if
     * you require direct access, with the exclusive lock command if you will be sending several commands or changing the adapter mode
     */
    @Override public String[] getPIDRawResponse(String OBDCommand) throws android.os.RemoteException
    {
      return null;
    }
    /** Get the protocol being used (or 0 if not currently connected) - the protocol number is as per the ELM327 documentation */
    @Override public int getProtocolNumber() throws android.os.RemoteException
    {
      return 0;
    }
    /** Get the protocol name being used (or AUTO if not currently connected) */
    @Override public String getProtocolName() throws android.os.RemoteException
    {
      return null;
    }
    /** Used by the dash tracker plugin, NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
    @Override public boolean setCurrentDashboard(String dashboardName, int hashKey) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Same as listAllPIDs except it returns detected sensors as well (these are generally not available as PIDs, but are sensors discovered in the course of talking to
     * the vehicle ECU (usually from other ECUs replying)
     */
    @Override public String[] listAllPIDsIncludingDetectedPIDs() throws android.os.RemoteException
    {
      return null;
    }
    /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
    @Override public String getSettingString(String setting, String def) throws android.os.RemoteException
    {
      return null;
    }
    /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
    @Override public boolean getSettingBoolean(String setting, boolean def) throws android.os.RemoteException
    {
      return false;
    }
    /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
    @Override public long getSettingLong(String setting, long def) throws android.os.RemoteException
    {
      return 0L;
    }
    /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
    @Override public int getSettingInt(String setting, int def) throws android.os.RemoteException
    {
      return 0;
    }
    /** Get the theme type (day/night)  NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
    @Override public String getThemeType() throws android.os.RemoteException
    {
      return null;
    }
    /** Calibrate the accelerometer for the current device angle */
    @Override public boolean calibrateAccelerometer() throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Get the current theme files as a URIs so we can get at it using content providers.
     * 
     * @param callingPackageName is the name of your package - eg 'my.someapp.stuff' so that uri permissions can be correctly granted
     */
    @Override public String[] getCurrentThemeUri(String callingPackageName) throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Sends a list of themes (one per call) to Torque (which will remember them until it is quit or
     * restarted or sends out another THEME_QUERY broadcast)
     * 
     * Make sure your FileProvider for the file Uris is setup correctly, do not forget to grant read
     * only permission. If the user selects the theme then torque will pull the file uris and copy
     * to local (to the app) storage
     * 
     * returns true if successful, false if not.
     */
    @Override public boolean putThemeData(String packageName, String themeName, String description, String author, String thumbnailUri, String[] themeFileUris) throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Return the current value only for the PID (or PIDs if you pass more than one)
     * 
     * Use this method to frequently get the value of PID(s).  Be aware that the more PIDs you request, the slower the update speed (as the OBD2 adapter will require to update more PIDs and this is the choke-point)
     * This method is asynchronous updates via the adapter
     */
    @Override public double[] getPIDValuesAsDouble(String[] pidsToRetrieve) throws android.os.RemoteException
    {
      return null;
    }
    /** Gets the current header in use (or null if no specific header has been set and the adapter default is being used) */
    @Override public String getCurrentHeader() throws android.os.RemoteException
    {
      return null;
    }
    /**
     * Utility method to ease the recombination of multiple CAN/etc frames from multiple ECUs
     * 
     * Basically put your response[] from sendCommandGetResponse() here and it will be combined from
     * several ECU responses into one coherent response (or more if more than one ECU responded).
     * Also handles out-of-order responses.
     */
    @Override public String[] recombineResponses(String[] rawResponsesFromECU) throws android.os.RemoteException
    {
      return null;
    }
    /** Returns true if Torque is using headers enabled (ATH1) with this connection */
    @Override public boolean areHeadersEnabled() throws android.os.RemoteException
    {
      return false;
    }
    /**
     * Captures frames directly using the AT MA command - useful for CANBUS systems
     * 
     * time - the time to wait whilst capturing in milliseconds. A value of 50ms should be enough
     * for every adapter type without causing the buffers to become full on fast systems.
     * 
     * returns an empty string if nothing was captured or the adapter was not in the correct state (still connecting to ECU, etc)
     */
    @Override public String[] captureFrames(int time) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  abstract class Stub extends android.os.Binder implements ITorqueService
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an org.prowl.torque.remote.ITorqueService interface,
     * generating a proxy if needed.
     */
    public static ITorqueService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof ITorqueService))) {
        return ((ITorqueService)iin);
      }
      return new Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_getVersion:
        {
          int _result = this.getVersion();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getValueForPid:
        {
          long _arg0;
          _arg0 = data.readLong();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          float _result = this.getValueForPid(_arg0, _arg1);
          reply.writeNoException();
          reply.writeFloat(_result);
          break;
        }
        case TRANSACTION_getDescriptionForPid:
        {
          long _arg0;
          _arg0 = data.readLong();
          String _result = this.getDescriptionForPid(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getShortNameForPid:
        {
          long _arg0;
          _arg0 = data.readLong();
          String _result = this.getShortNameForPid(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getUnitForPid:
        {
          long _arg0;
          _arg0 = data.readLong();
          String _result = this.getUnitForPid(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getMinValueForPid:
        {
          long _arg0;
          _arg0 = data.readLong();
          float _result = this.getMinValueForPid(_arg0);
          reply.writeNoException();
          reply.writeFloat(_result);
          break;
        }
        case TRANSACTION_getMaxValueForPid:
        {
          long _arg0;
          _arg0 = data.readLong();
          float _result = this.getMaxValueForPid(_arg0);
          reply.writeNoException();
          reply.writeFloat(_result);
          break;
        }
        case TRANSACTION_getListOfActivePids:
        {
          long[] _result = this.getListOfActivePids();
          reply.writeNoException();
          reply.writeLongArray(_result);
          break;
        }
        case TRANSACTION_getListOfECUSupportedPids:
        {
          long[] _result = this.getListOfECUSupportedPids();
          reply.writeNoException();
          reply.writeLongArray(_result);
          break;
        }
        case TRANSACTION_getListOfAllPids:
        {
          long[] _result = this.getListOfAllPids();
          reply.writeNoException();
          reply.writeLongArray(_result);
          break;
        }
        case TRANSACTION_hasFullPermissions:
        {
          boolean _result = this.hasFullPermissions();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_sendCommandGetResponse:
        {
          String _arg0;
          _arg0 = data.readString();
          String _arg1;
          _arg1 = data.readString();
          String[] _result = this.sendCommandGetResponse(_arg0, _arg1);
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_getPreferredUnit:
        {
          String _arg0;
          _arg0 = data.readString();
          String _result = this.getPreferredUnit(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_setPIDData:
        {
          String _arg0;
          _arg0 = data.readString();
          String _arg1;
          _arg1 = data.readString();
          String _arg2;
          _arg2 = data.readString();
          float _arg3;
          _arg3 = data.readFloat();
          float _arg4;
          _arg4 = data.readFloat();
          float _arg5;
          _arg5 = data.readFloat();
          boolean _result = this.setPIDData(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_isConnectedToECU:
        {
          boolean _result = this.isConnectedToECU();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setDebugTestMode:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          boolean _result = this.setDebugTestMode(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getVehicleProfileInformation:
        {
          String[] _result = this.getVehicleProfileInformation();
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_storeInProfile:
        {
          String _arg0;
          _arg0 = data.readString();
          String _arg1;
          _arg1 = data.readString();
          boolean _arg2;
          _arg2 = (0!=data.readInt());
          int _result = this.storeInProfile(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_retrieveProfileData:
        {
          String _arg0;
          _arg0 = data.readString();
          String _result = this.retrieveProfileData(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getDataErrorCount:
        {
          int _result = this.getDataErrorCount();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getPIDReadSpeed:
        {
          double _result = this.getPIDReadSpeed();
          reply.writeNoException();
          reply.writeDouble(_result);
          break;
        }
        case TRANSACTION_getConfiguredSpeed:
        {
          int _result = this.getConfiguredSpeed();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_isFileLoggingEnabled:
        {
          boolean _result = this.isFileLoggingEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_isWebLoggingEnabled:
        {
          boolean _result = this.isWebLoggingEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getNumberOfLoggedItems:
        {
          int _result = this.getNumberOfLoggedItems();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getUpdateTimeForPID:
        {
          long _arg0;
          _arg0 = data.readLong();
          long _result = this.getUpdateTimeForPID(_arg0);
          reply.writeNoException();
          reply.writeLong(_result);
          break;
        }
        case TRANSACTION_getScaleForPid:
        {
          long _arg0;
          _arg0 = data.readLong();
          float _result = this.getScaleForPid(_arg0);
          reply.writeNoException();
          reply.writeFloat(_result);
          break;
        }
        case TRANSACTION_translate:
        {
          String _arg0;
          _arg0 = data.readString();
          String _result = this.translate(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_sendPIDData:
        {
          String _arg0;
          _arg0 = data.readString();
          String[] _arg1;
          _arg1 = data.createStringArray();
          String[] _arg2;
          _arg2 = data.createStringArray();
          String[] _arg3;
          _arg3 = data.createStringArray();
          String[] _arg4;
          _arg4 = data.createStringArray();
          float[] _arg5;
          _arg5 = data.createFloatArray();
          float[] _arg6;
          _arg6 = data.createFloatArray();
          String[] _arg7;
          _arg7 = data.createStringArray();
          String[] _arg8;
          _arg8 = data.createStringArray();
          boolean _result = this.sendPIDData(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setPIDInformation:
        {
          String _arg0;
          _arg0 = data.readString();
          String _arg1;
          _arg1 = data.readString();
          String _arg2;
          _arg2 = data.readString();
          float _arg3;
          _arg3 = data.readFloat();
          float _arg4;
          _arg4 = data.readFloat();
          float _arg5;
          _arg5 = data.readFloat();
          String _arg6;
          _arg6 = data.readString();
          boolean _result = this.setPIDInformation(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_listAllPIDs:
        {
          String[] _result = this.listAllPIDs();
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_listActivePIDs:
        {
          String[] _result = this.listActivePIDs();
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_listECUSupportedPIDs:
        {
          String[] _result = this.listECUSupportedPIDs();
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_getPIDValues:
        {
          String[] _arg0;
          _arg0 = data.createStringArray();
          float[] _result = this.getPIDValues(_arg0);
          reply.writeNoException();
          reply.writeFloatArray(_result);
          break;
        }
        case TRANSACTION_getPIDInformation:
        {
          String[] _arg0;
          _arg0 = data.createStringArray();
          String[] _result = this.getPIDInformation(_arg0);
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_getPIDUpdateTime:
        {
          String[] _arg0;
          _arg0 = data.createStringArray();
          long[] _result = this.getPIDUpdateTime(_arg0);
          reply.writeNoException();
          reply.writeLongArray(_result);
          break;
        }
        case TRANSACTION_getValueForPids:
        {
          long[] _arg0;
          _arg0 = data.createLongArray();
          float[] _result = this.getValueForPids(_arg0);
          reply.writeNoException();
          reply.writeFloatArray(_result);
          break;
        }
        case TRANSACTION_sendPIDDataPrivate:
        {
          String _arg0;
          _arg0 = data.readString();
          String[] _arg1;
          _arg1 = data.createStringArray();
          String[] _arg2;
          _arg2 = data.createStringArray();
          String[] _arg3;
          _arg3 = data.createStringArray();
          String[] _arg4;
          _arg4 = data.createStringArray();
          float[] _arg5;
          _arg5 = data.createFloatArray();
          float[] _arg6;
          _arg6 = data.createFloatArray();
          String[] _arg7;
          _arg7 = data.createStringArray();
          String[] _arg8;
          _arg8 = data.createStringArray();
          boolean _result = this.sendPIDDataPrivate(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_requestExclusiveLock:
        {
          String _arg0;
          _arg0 = data.readString();
          int _result = this.requestExclusiveLock(_arg0);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_releaseExclusiveLock:
        {
          String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          boolean _result = this.releaseExclusiveLock(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_sendPIDDataV2:
        {
          String _arg0;
          _arg0 = data.readString();
          String[] _arg1;
          _arg1 = data.createStringArray();
          String[] _arg2;
          _arg2 = data.createStringArray();
          String[] _arg3;
          _arg3 = data.createStringArray();
          String[] _arg4;
          _arg4 = data.createStringArray();
          float[] _arg5;
          _arg5 = data.createFloatArray();
          float[] _arg6;
          _arg6 = data.createFloatArray();
          String[] _arg7;
          _arg7 = data.createStringArray();
          String[] _arg8;
          _arg8 = data.createStringArray();
          String[] _arg9;
          _arg9 = data.createStringArray();
          String[] _arg10;
          _arg10 = data.createStringArray();
          boolean _result = this.sendPIDDataV2(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_sendPIDDataPrivateV2:
        {
          String _arg0;
          _arg0 = data.readString();
          String[] _arg1;
          _arg1 = data.createStringArray();
          String[] _arg2;
          _arg2 = data.createStringArray();
          String[] _arg3;
          _arg3 = data.createStringArray();
          String[] _arg4;
          _arg4 = data.createStringArray();
          float[] _arg5;
          _arg5 = data.createFloatArray();
          float[] _arg6;
          _arg6 = data.createFloatArray();
          String[] _arg7;
          _arg7 = data.createStringArray();
          String[] _arg8;
          _arg8 = data.createStringArray();
          String[] _arg9;
          _arg9 = data.createStringArray();
          String[] _arg10;
          _arg10 = data.createStringArray();
          boolean _result = this.sendPIDDataPrivateV2(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getPIDRawResponse:
        {
          String _arg0;
          _arg0 = data.readString();
          String[] _result = this.getPIDRawResponse(_arg0);
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_getProtocolNumber:
        {
          int _result = this.getProtocolNumber();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getProtocolName:
        {
          String _result = this.getProtocolName();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_setCurrentDashboard:
        {
          String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          boolean _result = this.setCurrentDashboard(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_listAllPIDsIncludingDetectedPIDs:
        {
          String[] _result = this.listAllPIDsIncludingDetectedPIDs();
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_getSettingString:
        {
          String _arg0;
          _arg0 = data.readString();
          String _arg1;
          _arg1 = data.readString();
          String _result = this.getSettingString(_arg0, _arg1);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_getSettingBoolean:
        {
          String _arg0;
          _arg0 = data.readString();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          boolean _result = this.getSettingBoolean(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getSettingLong:
        {
          String _arg0;
          _arg0 = data.readString();
          long _arg1;
          _arg1 = data.readLong();
          long _result = this.getSettingLong(_arg0, _arg1);
          reply.writeNoException();
          reply.writeLong(_result);
          break;
        }
        case TRANSACTION_getSettingInt:
        {
          String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          int _result = this.getSettingInt(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getThemeType:
        {
          String _result = this.getThemeType();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_calibrateAccelerometer:
        {
          boolean _result = this.calibrateAccelerometer();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getCurrentThemeUri:
        {
          String _arg0;
          _arg0 = data.readString();
          String[] _result = this.getCurrentThemeUri(_arg0);
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_putThemeData:
        {
          String _arg0;
          _arg0 = data.readString();
          String _arg1;
          _arg1 = data.readString();
          String _arg2;
          _arg2 = data.readString();
          String _arg3;
          _arg3 = data.readString();
          String _arg4;
          _arg4 = data.readString();
          String[] _arg5;
          _arg5 = data.createStringArray();
          boolean _result = this.putThemeData(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_getPIDValuesAsDouble:
        {
          String[] _arg0;
          _arg0 = data.createStringArray();
          double[] _result = this.getPIDValuesAsDouble(_arg0);
          reply.writeNoException();
          reply.writeDoubleArray(_result);
          break;
        }
        case TRANSACTION_getCurrentHeader:
        {
          String _result = this.getCurrentHeader();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_recombineResponses:
        {
          String[] _arg0;
          _arg0 = data.createStringArray();
          String[] _result = this.recombineResponses(_arg0);
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        case TRANSACTION_areHeadersEnabled:
        {
          boolean _result = this.areHeadersEnabled();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_captureFrames:
        {
          int _arg0;
          _arg0 = data.readInt();
          String[] _result = this.captureFrames(_arg0);
          reply.writeNoException();
          reply.writeStringArray(_result);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements ITorqueService
    {
      private final android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /** Get the API version. The version will increment each time the API is revised. */
      @Override public int getVersion() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVersion, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the most recent value stored for the given PID.  This will return immediately whether or not data exists.
       * @param triggersDataRefresh Cause the data to be re-requested from the ECU
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public float getValueForPid(long pid, boolean triggersDataRefresh) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        float _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          _data.writeInt(((triggersDataRefresh)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getValueForPid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readFloat();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get a textual, long description regarding the PID, already translated (when translation is implemented)
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public String getDescriptionForPid(long pid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getDescriptionForPid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the shortname of the PID
       * @note If the PID returns multiple values, then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public String getShortNameForPid(long pid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getShortNameForPid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the Si unit in string form for the PID, if no Si unit is available, a textual-description is returned instead.
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public String getUnitForPid(long pid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getUnitForPid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the minimum value expected for this PID
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public float getMinValueForPid(long pid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        float _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getMinValueForPid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readFloat();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the maximum value expected for this PId
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public float getMaxValueForPid(long pid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        float _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getMaxValueForPid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readFloat();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns a list of currently 'active' PIDs. This list will change often. Try not to call this method too frequently.
       * 
       * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public long[] getListOfActivePids() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getListOfActivePids, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createLongArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns a list of PIDs that have been reported by the ECU as supported.
       * 
       * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public long[] getListOfECUSupportedPids() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getListOfECUSupportedPids, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createLongArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns a list of all the known available sensors, active or inactive.
       * 
       * Try not to call this method too frequently.
       * 
       * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       * @deprecated  See getPIDInformation(...), getPIDValues(...)  and listAllPIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public long[] getListOfAllPids() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getListOfAllPids, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createLongArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** True if the user has granted full permissions to the plugin (to send anything that it wants) */
      @Override public boolean hasFullPermissions() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_hasFullPermissions, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Send a specific request over the OBD bus and return the response as a string
       * 
       * This is currently limited by the 'Allow full permissions' option in the settings.
       * 
       * Modes 01,02,03,07,09,0a,21,22 and 23 are permitted. Permission needs to be granted in the
       * app settings for any other modes.
       */
      @Override public String[] sendCommandGetResponse(String header, String command) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(header);
          _data.writeString(command);
          boolean _status = mRemote.transact(Stub.TRANSACTION_sendCommandGetResponse, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Given this unit, get the users preferred units */
      @Override public String getPreferredUnit(String unit) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(unit);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPreferredUnit, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Add / Update a PID from your plugin to the main app, Your PID is keyed by name.
       * 
       * @deprecated - (see below)superceded by setPIDInformation(String name, String shortName, String unit, float max, float min, float value, String stringValue);
       */
      @Override public boolean setPIDData(String name, String shortName, String unit, float max, float min, float value) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(name);
          _data.writeString(shortName);
          _data.writeString(unit);
          _data.writeFloat(max);
          _data.writeFloat(min);
          _data.writeFloat(value);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPIDData, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get connection state to ECU
       * @return true if connected to the ECU, false if the app has not yet started retrieving data from the ECU
       */
      @Override public boolean isConnectedToECU() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isConnectedToECU, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Turn on or off test mode. This is for debugging only and simulates readings to some of the sensors. */
      @Override public boolean setDebugTestMode(boolean activateTestMode) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((activateTestMode)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setDebugTestMode, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns a string array containing the vehicle profile information:
       * 
       *  Array:
       *     [0] Profile name
       *     [1] Engine Displacement (L)
       *     [2] Weight in kilogrammes
       *     [3] Fuel type (0 = Petrol, 1 = Diesel, 2 = E85 (Ethanol/Petrol)    *     [4] Boost adjustment
       *     [5] Max RPM setting
       *     [6] Volumetric efficiency (%)
       *     [7] Accumulated distance travelled
       */
      @Override public String[] getVehicleProfileInformation() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVehicleProfileInformation, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Store some information into the vehicle profile.
       * 
       * @param key  Prefix the 'key' with your apps classpath (to avoid conflicts). eg: "com.company.pluginname.SOME_KEY_NAME" is nice and clear  (Don't use any characters other than A-Za-z0-9 . and _)
       * @param The value to store.
       * @param saveToFile Set this to true (on your last 'set') to commit the information to disk.
       * @return 0 if successful.
       */
      @Override public int storeInProfile(String key, String value, boolean saveToFileNow) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(key);
          _data.writeString(value);
          _data.writeInt(((saveToFileNow)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_storeInProfile, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Retrieve some information from the vehicle profile.
       * 
       * @param Prefix the 'key' with your apps classpath (to avoid conflicts).
       */
      @Override public String retrieveProfileData(String key) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(key);
          boolean _status = mRemote.transact(Stub.TRANSACTION_retrieveProfileData, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Retrieve the number of errors that Torque has seen happen at the adapter
       * 
       * Generally speaking, if this is above 0, then you have problems at the adapter side.
       */
      @Override public int getDataErrorCount() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getDataErrorCount, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get max PID read speed in PIDs read per second
       * 
       * This is reset each connection to the adpater.
       */
      @Override public double getPIDReadSpeed() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        double _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPIDReadSpeed, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readDouble();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Gets the configured communications speed
       * 
       *  1 == slowest (for problematic adapters)
       *  2 == standard (The app default)
       *  3 == fast mode (selectable in the OBD2 adapter settings)
       * 
       * Fast mode provides a significant speed increase on some vehicles for the read speed of sensor information
       */
      @Override public int getConfiguredSpeed() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getConfiguredSpeed, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** returns true if the user is currently logging data to the file */
      @Override public boolean isFileLoggingEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isFileLoggingEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** returns true if the user is currently logging data to web */
      @Override public boolean isWebLoggingEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isWebLoggingEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** returns the number of items that are configured to be logged to file. The more items configured to log (and with logging enabled), the slower the refresh rate of individual PIDs. */
      @Override public int getNumberOfLoggedItems() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getNumberOfLoggedItems, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the time that the PID was last updated.
       * 
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       */
      @Override public long getUpdateTimeForPID(long pid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getUpdateTimeForPID, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Returns the scale of the requested PID
       * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
       */
      @Override public float getScaleForPid(long pid) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        float _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLong(pid);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getScaleForPid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readFloat();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Translate a string that *might* be in translation file in the main Torque app. Do not call this method repeatedly for the same text! */
      @Override public String translate(String originalText) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(originalText);
          boolean _status = mRemote.transact(Stub.TRANSACTION_translate, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Method to send PID data in raw form.
       *  Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header
       * @deprecated - see sendPIDDataV2 (which allows diagnostic headers to be set, or left as null)
       */
      @Override public boolean sendPIDData(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pluginName);
          _data.writeStringArray(name);
          _data.writeStringArray(shortName);
          _data.writeStringArray(modeAndPID);
          _data.writeStringArray(equation);
          _data.writeFloatArray(minValue);
          _data.writeFloatArray(maxValue);
          _data.writeStringArray(units);
          _data.writeStringArray(header);
          boolean _status = mRemote.transact(Stub.TRANSACTION_sendPIDData, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Add / Update a PID from your plugin to the main app, Your PID is keyed by name.  This can be used to set a value on a PID in Torque.
       * 
       * @param value       The value to be shown in the display
       * @param stringValue A string value to be shown in the display - overrides 'value' when not null - note(try not to use numeric values here as they won't be unit converted for you).  Set to null to revert to using the 'float value'
       */
      @Override public boolean setPIDInformation(String name, String shortName, String unit, float max, float min, float value, String stringValue) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(name);
          _data.writeString(shortName);
          _data.writeString(unit);
          _data.writeFloat(max);
          _data.writeFloat(min);
          _data.writeFloat(value);
          _data.writeString(stringValue);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPIDInformation, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get a list of all PIDs in torque, active or inactive.
       * 
       * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
       * @note (don't call this too often as it is computationally expensive!)
       */
      @Override public String[] listAllPIDs() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_listAllPIDs, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * List all only the active PIDs
       * 
       * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
       * @note (don't call this too often as it is computationally expensive!)
       */
      @Override public String[] listActivePIDs() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_listActivePIDs, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * List all only the active PIDs
       * 
       * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
       * @note (don't call this too often as it is computationally expensive!)
       */
      @Override public String[] listECUSupportedPIDs() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_listECUSupportedPIDs, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Return the current value only for the PID (or PIDs if you pass more than one)
       * 
       * Use this method to frequently get the value of PID(s).  Be aware that the more PIDs you request, the slower the update speed (as the OBD2 adapter will require to update more PIDs and this is the choke-point)
       * This method is asynchronous updates via the adapter
       * 
       * @deprecated please use getPIDValuesAsDouble(...) instead
       */
      @Override public float[] getPIDValues(String[] pidsToRetrieve) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        float[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringArray(pidsToRetrieve);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPIDValues, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createFloatArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Return all the PID information in a single transaction. This is the new preferred method to get data from PIDs
       * 
       * The unit returned is the raw unit (used for getPIDValues) - you can get the users preferred unit by using the relevant API call
       * 
       * Format of returned string array:
       *  String[] {
       *       String "pid information in csv format"
       *       String "pid information in csv format"
       *       ...(etc)
       *    }
       * 
       *  Example:
       * 
       *   String[] {
       *      "<longName>,<shortName>,<unit>,<maxValue>,<minValue>,<scale>",
       *      "Intake Manifold Pressure,Intake,kPa,255,0,1",
       *      "Accelerator Pedal Position E,Accel(E),%,100,0,1"
       *   }
       */
      @Override public String[] getPIDInformation(String[] pidIDs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringArray(pidIDs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPIDInformation, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the time that the PID was last updated.
       * 
       * @returns time when the PID value was retrieved via OBD/etc in milliseconds
       */
      @Override public long[] getPIDUpdateTime(String[] pidIDs) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringArray(pidIDs);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPIDUpdateTime, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createLongArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Retrieves a list of PID values
       * 
       * @deprecated See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
       */
      @Override public float[] getValueForPids(long[] pids) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        float[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeLongArray(pids);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getValueForPids, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createFloatArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Method to send PID data in raw form - this is for when pid plugin vendors want to protect their hard work when they have decyphed or licensed PIDs
       * Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header
       * @deprecated - see sendPIDDataPrivateV2 (which allows diagnostic headers to be set, or left as null)
       */
      @Override public boolean sendPIDDataPrivate(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pluginName);
          _data.writeStringArray(name);
          _data.writeStringArray(shortName);
          _data.writeStringArray(modeAndPID);
          _data.writeStringArray(equation);
          _data.writeFloatArray(minValue);
          _data.writeFloatArray(maxValue);
          _data.writeStringArray(units);
          _data.writeStringArray(header);
          boolean _status = mRemote.transact(Stub.TRANSACTION_sendPIDDataPrivate, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Cause Torque to stop communicating with the adpter so external plugins can take full control
       * 
       * This is when you want to do special things like reconfigure the adpater to talk to special units (ABS, SRS, Body control, etc)
       * which may require torque to stay away from asking information from the adpater
       * 
       * @return an int depicting the state of the lock
       * 
       *   0 = Lock gained OK - you now have exclusive access
       *  -1 = Lock failed due to unknown reason
       *  -2 = Lock failed due to another lock already being present (from your, or another plugin)
       *  -3 = Lock failed due to not being connected to adapter
       *  -4 = reserved - you still didn't get a lock.
       *  -5 = No permissions to access in this manner (enable the ticky-box in the plugin settings for 'full access')
       *  -6 = Lock failed due to timeout trying to get a lock (10 second limit hit)
       */
      @Override public int requestExclusiveLock(String pluginName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pluginName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_requestExclusiveLock, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Release the exclusive lock the plugin has with the adapter.
       * 
       * @param torqueMustReInitializeTheAdapter set this to TRUE if torque must reinit comms with the vehicle ECU to continue communicating - if it is set to FALSE, then torque will simply continue trying to talk to the ECU. Take special care to ensure the adpater is in the proper state to do this
       */
      @Override public boolean releaseExclusiveLock(String pluginName, boolean torqueMustReInitializeTheAdapter) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pluginName);
          _data.writeInt(((torqueMustReInitializeTheAdapter)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_releaseExclusiveLock, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Method to send PID data in raw form.
       * This updated method allows diagnostic headers to be set if required (for special commands) or left as null.
       *  Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header,Start diagnostic command, Stop diagnostic command
       */
      @Override public boolean sendPIDDataV2(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header, String[] startDiagnostic, String[] stopDiagnostic) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pluginName);
          _data.writeStringArray(name);
          _data.writeStringArray(shortName);
          _data.writeStringArray(modeAndPID);
          _data.writeStringArray(equation);
          _data.writeFloatArray(minValue);
          _data.writeFloatArray(maxValue);
          _data.writeStringArray(units);
          _data.writeStringArray(header);
          _data.writeStringArray(startDiagnostic);
          _data.writeStringArray(stopDiagnostic);
          boolean _status = mRemote.transact(Stub.TRANSACTION_sendPIDDataV2, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Method to send PID data in raw form - this is for when pid plugin vendors want to protect their hard work when they have decyphed or licensed PIDs
       * This updated method allows diagnostic headers to be set if required (for special commands) or left as null.
       * Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header, Start diagnostic command, Stop diagnostic command
       */
      @Override public boolean sendPIDDataPrivateV2(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header, String[] startDiagnostic, String[] stopDiagnostic) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(pluginName);
          _data.writeStringArray(name);
          _data.writeStringArray(shortName);
          _data.writeStringArray(modeAndPID);
          _data.writeStringArray(equation);
          _data.writeFloatArray(minValue);
          _data.writeFloatArray(maxValue);
          _data.writeStringArray(units);
          _data.writeStringArray(header);
          _data.writeStringArray(startDiagnostic);
          _data.writeStringArray(stopDiagnostic);
          boolean _status = mRemote.transact(Stub.TRANSACTION_sendPIDDataPrivateV2, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * This retrieves the last PID that torque read from the adapter in RAW form (as the adapter sent it) 'NOT READY' may be returned
       * if the adapter has not yet retrieved that PID. The OBDCommand should be as sent to the adpater (no spaces - eg: '010D')
       * 
       * Despite the name, this cannot be used to send commands directly to the adapter, use the sendCommandGetResponse(...) method if
       * you require direct access, with the exclusive lock command if you will be sending several commands or changing the adapter mode
       */
      @Override public String[] getPIDRawResponse(String OBDCommand) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(OBDCommand);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPIDRawResponse, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Get the protocol being used (or 0 if not currently connected) - the protocol number is as per the ELM327 documentation */
      @Override public int getProtocolNumber() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getProtocolNumber, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Get the protocol name being used (or AUTO if not currently connected) */
      @Override public String getProtocolName() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getProtocolName, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Used by the dash tracker plugin, NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
      @Override public boolean setCurrentDashboard(String dashboardName, int hashKey) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(dashboardName);
          _data.writeInt(hashKey);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setCurrentDashboard, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Same as listAllPIDs except it returns detected sensors as well (these are generally not available as PIDs, but are sensors discovered in the course of talking to
       * the vehicle ECU (usually from other ECUs replying)
       */
      @Override public String[] listAllPIDsIncludingDetectedPIDs() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_listAllPIDsIncludingDetectedPIDs, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
      @Override public String getSettingString(String setting, String def) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(setting);
          _data.writeString(def);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSettingString, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
      @Override public boolean getSettingBoolean(String setting, boolean def) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(setting);
          _data.writeInt(((def)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSettingBoolean, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
      @Override public long getSettingLong(String setting, long def) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        long _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(setting);
          _data.writeLong(def);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSettingLong, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readLong();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
      @Override public int getSettingInt(String setting, int def) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(setting);
          _data.writeInt(def);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSettingInt, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Get the theme type (day/night)  NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
      @Override public String getThemeType() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getThemeType, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Calibrate the accelerometer for the current device angle */
      @Override public boolean calibrateAccelerometer() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_calibrateAccelerometer, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Get the current theme files as a URIs so we can get at it using content providers.
       * 
       * @param callingPackageName is the name of your package - eg 'my.someapp.stuff' so that uri permissions can be correctly granted
       */
      @Override public String[] getCurrentThemeUri(String callingPackageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(callingPackageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getCurrentThemeUri, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Sends a list of themes (one per call) to Torque (which will remember them until it is quit or
       * restarted or sends out another THEME_QUERY broadcast)
       * 
       * Make sure your FileProvider for the file Uris is setup correctly, do not forget to grant read
       * only permission. If the user selects the theme then torque will pull the file uris and copy
       * to local (to the app) storage
       * 
       * returns true if successful, false if not.
       */
      @Override public boolean putThemeData(String packageName, String themeName, String description, String author, String thumbnailUri, String[] themeFileUris) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(themeName);
          _data.writeString(description);
          _data.writeString(author);
          _data.writeString(thumbnailUri);
          _data.writeStringArray(themeFileUris);
          boolean _status = mRemote.transact(Stub.TRANSACTION_putThemeData, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Return the current value only for the PID (or PIDs if you pass more than one)
       * 
       * Use this method to frequently get the value of PID(s).  Be aware that the more PIDs you request, the slower the update speed (as the OBD2 adapter will require to update more PIDs and this is the choke-point)
       * This method is asynchronous updates via the adapter
       */
      @Override public double[] getPIDValuesAsDouble(String[] pidsToRetrieve) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        double[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringArray(pidsToRetrieve);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPIDValuesAsDouble, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createDoubleArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Gets the current header in use (or null if no specific header has been set and the adapter default is being used) */
      @Override public String getCurrentHeader() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getCurrentHeader, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Utility method to ease the recombination of multiple CAN/etc frames from multiple ECUs
       * 
       * Basically put your response[] from sendCommandGetResponse() here and it will be combined from
       * several ECU responses into one coherent response (or more if more than one ECU responded).
       * Also handles out-of-order responses.
       */
      @Override public String[] recombineResponses(String[] rawResponsesFromECU) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringArray(rawResponsesFromECU);
          boolean _status = mRemote.transact(Stub.TRANSACTION_recombineResponses, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /** Returns true if Torque is using headers enabled (ATH1) with this connection */
      @Override public boolean areHeadersEnabled() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_areHeadersEnabled, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
       * Captures frames directly using the AT MA command - useful for CANBUS systems
       * 
       * time - the time to wait whilst capturing in milliseconds. A value of 50ms should be enough
       * for every adapter type without causing the buffers to become full on fast systems.
       * 
       * returns an empty string if nothing was captured or the adapter was not in the correct state (still connecting to ECU, etc)
       */
      @Override public String[] captureFrames(int time) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(time);
          boolean _status = mRemote.transact(Stub.TRANSACTION_captureFrames, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    static final int TRANSACTION_getVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION);
    static final int TRANSACTION_getValueForPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getDescriptionForPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getShortNameForPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getUnitForPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_getMinValueForPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_getMaxValueForPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_getListOfActivePids = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_getListOfECUSupportedPids = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_getListOfAllPids = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_hasFullPermissions = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_sendCommandGetResponse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_getPreferredUnit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_setPIDData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_isConnectedToECU = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    static final int TRANSACTION_setDebugTestMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    static final int TRANSACTION_getVehicleProfileInformation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
    static final int TRANSACTION_storeInProfile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
    static final int TRANSACTION_retrieveProfileData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
    static final int TRANSACTION_getDataErrorCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
    static final int TRANSACTION_getPIDReadSpeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
    static final int TRANSACTION_getConfiguredSpeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    static final int TRANSACTION_isFileLoggingEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
    static final int TRANSACTION_isWebLoggingEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
    static final int TRANSACTION_getNumberOfLoggedItems = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
    static final int TRANSACTION_getUpdateTimeForPID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
    static final int TRANSACTION_getScaleForPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
    static final int TRANSACTION_translate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
    static final int TRANSACTION_sendPIDData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
    static final int TRANSACTION_setPIDInformation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
    static final int TRANSACTION_listAllPIDs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
    static final int TRANSACTION_listActivePIDs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
    static final int TRANSACTION_listECUSupportedPIDs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
    static final int TRANSACTION_getPIDValues = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
    static final int TRANSACTION_getPIDInformation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
    static final int TRANSACTION_getPIDUpdateTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
    static final int TRANSACTION_getValueForPids = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
    static final int TRANSACTION_sendPIDDataPrivate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
    static final int TRANSACTION_requestExclusiveLock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
    static final int TRANSACTION_releaseExclusiveLock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
    static final int TRANSACTION_sendPIDDataV2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
    static final int TRANSACTION_sendPIDDataPrivateV2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
    static final int TRANSACTION_getPIDRawResponse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
    static final int TRANSACTION_getProtocolNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
    static final int TRANSACTION_getProtocolName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
    static final int TRANSACTION_setCurrentDashboard = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
    static final int TRANSACTION_listAllPIDsIncludingDetectedPIDs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 46);
    static final int TRANSACTION_getSettingString = (android.os.IBinder.FIRST_CALL_TRANSACTION + 47);
    static final int TRANSACTION_getSettingBoolean = (android.os.IBinder.FIRST_CALL_TRANSACTION + 48);
    static final int TRANSACTION_getSettingLong = (android.os.IBinder.FIRST_CALL_TRANSACTION + 49);
    static final int TRANSACTION_getSettingInt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 50);
    static final int TRANSACTION_getThemeType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 51);
    static final int TRANSACTION_calibrateAccelerometer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 52);
    static final int TRANSACTION_getCurrentThemeUri = (android.os.IBinder.FIRST_CALL_TRANSACTION + 53);
    static final int TRANSACTION_putThemeData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 54);
    static final int TRANSACTION_getPIDValuesAsDouble = (android.os.IBinder.FIRST_CALL_TRANSACTION + 55);
    static final int TRANSACTION_getCurrentHeader = (android.os.IBinder.FIRST_CALL_TRANSACTION + 56);
    static final int TRANSACTION_recombineResponses = (android.os.IBinder.FIRST_CALL_TRANSACTION + 57);
    static final int TRANSACTION_areHeadersEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 58);
    static final int TRANSACTION_captureFrames = (android.os.IBinder.FIRST_CALL_TRANSACTION + 59);
  }
  String DESCRIPTOR = "org.prowl.torque.remote.ITorqueService";
  /** Get the API version. The version will increment each time the API is revised. */
  int getVersion() throws android.os.RemoteException;
  /**
   * Get the most recent value stored for the given PID.  This will return immediately whether or not data exists.
   * @param triggersDataRefresh Cause the data to be re-requested from the ECU
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  float getValueForPid(long pid, boolean triggersDataRefresh) throws android.os.RemoteException;
  /**
   * Get a textual, long description regarding the PID, already translated (when translation is implemented)
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  String getDescriptionForPid(long pid) throws android.os.RemoteException;
  /**
   * Get the shortname of the PID
   * @note If the PID returns multiple values, then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  String getShortNameForPid(long pid) throws android.os.RemoteException;
  /**
   * Get the Si unit in string form for the PID, if no Si unit is available, a textual-description is returned instead.
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  String getUnitForPid(long pid) throws android.os.RemoteException;
  /**
   * Get the minimum value expected for this PID
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  float getMinValueForPid(long pid) throws android.os.RemoteException;
  /**
   * Get the maximum value expected for this PId
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  float getMaxValueForPid(long pid) throws android.os.RemoteException;
  /**
   * Returns a list of currently 'active' PIDs. This list will change often. Try not to call this method too frequently.
   * 
   * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  long[] getListOfActivePids() throws android.os.RemoteException;
  /**
   * Returns a list of PIDs that have been reported by the ECU as supported.
   * 
   * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  long[] getListOfECUSupportedPids() throws android.os.RemoteException;
  /**
   * Returns a list of all the known available sensors, active or inactive.
   * 
   * Try not to call this method too frequently.
   * 
   * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   * @deprecated  See getPIDInformation(...), getPIDValues(...)  and listAllPIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  long[] getListOfAllPids() throws android.os.RemoteException;
  /** True if the user has granted full permissions to the plugin (to send anything that it wants) */
  boolean hasFullPermissions() throws android.os.RemoteException;
  /**
   * Send a specific request over the OBD bus and return the response as a string
   * 
   * This is currently limited by the 'Allow full permissions' option in the settings.
   * 
   * Modes 01,02,03,07,09,0a,21,22 and 23 are permitted. Permission needs to be granted in the
   * app settings for any other modes.
   */
  String[] sendCommandGetResponse(String header, String command) throws android.os.RemoteException;
  /** Given this unit, get the users preferred units */
  String getPreferredUnit(String unit) throws android.os.RemoteException;
  /**
   * Add / Update a PID from your plugin to the main app, Your PID is keyed by name.
   * 
   * @deprecated - (see below)superceded by setPIDInformation(String name, String shortName, String unit, float max, float min, float value, String stringValue);
   */
  @Deprecated
  boolean setPIDData(String name, String shortName, String unit, float max, float min, float value) throws android.os.RemoteException;
  /**
   * Get connection state to ECU
   * @return true if connected to the ECU, false if the app has not yet started retrieving data from the ECU
   */
  boolean isConnectedToECU() throws android.os.RemoteException;
  /** Turn on or off test mode. This is for debugging only and simulates readings to some of the sensors. */
  boolean setDebugTestMode(boolean activateTestMode) throws android.os.RemoteException;
  /**
   * Returns a string array containing the vehicle profile information:
   * 
   *  Array:
   *     [0] Profile name
   *     [1] Engine Displacement (L)
   *     [2] Weight in kilogrammes
   *     [3] Fuel type (0 = Petrol, 1 = Diesel, 2 = E85 (Ethanol/Petrol)    *     [4] Boost adjustment
   *     [5] Max RPM setting
   *     [6] Volumetric efficiency (%)
   *     [7] Accumulated distance travelled
   */
  String[] getVehicleProfileInformation() throws android.os.RemoteException;
  /**
   * Store some information into the vehicle profile.
   * 
   * @param key  Prefix the 'key' with your apps classpath (to avoid conflicts). eg: "com.company.pluginname.SOME_KEY_NAME" is nice and clear  (Don't use any characters other than A-Za-z0-9 . and _)
   * @param The value to store.
   * @param saveToFile Set this to true (on your last 'set') to commit the information to disk.
   * @return 0 if successful.
   */
  int storeInProfile(String key, String value, boolean saveToFileNow) throws android.os.RemoteException;
  /**
   * Retrieve some information from the vehicle profile.
   * 
   * @param Prefix the 'key' with your apps classpath (to avoid conflicts).
   */
  String retrieveProfileData(String key) throws android.os.RemoteException;
  /**
   * Retrieve the number of errors that Torque has seen happen at the adapter
   * 
   * Generally speaking, if this is above 0, then you have problems at the adapter side.
   */
  int getDataErrorCount() throws android.os.RemoteException;
  /**
   * Get max PID read speed in PIDs read per second
   * 
   * This is reset each connection to the adpater.
   */
  double getPIDReadSpeed() throws android.os.RemoteException;
  /**
   * Gets the configured communications speed
   * 
   *  1 == slowest (for problematic adapters)
   *  2 == standard (The app default)
   *  3 == fast mode (selectable in the OBD2 adapter settings)
   * 
   * Fast mode provides a significant speed increase on some vehicles for the read speed of sensor information
   */
  int getConfiguredSpeed() throws android.os.RemoteException;
  /** returns true if the user is currently logging data to the file */
  boolean isFileLoggingEnabled() throws android.os.RemoteException;
  /** returns true if the user is currently logging data to web */
  boolean isWebLoggingEnabled() throws android.os.RemoteException;
  /** returns the number of items that are configured to be logged to file. The more items configured to log (and with logging enabled), the slower the refresh rate of individual PIDs. */
  int getNumberOfLoggedItems() throws android.os.RemoteException;
  /**
   * Get the time that the PID was last updated.
   * 
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   */
  long getUpdateTimeForPID(long pid) throws android.os.RemoteException;
  /**
   * Returns the scale of the requested PID
   * @note If the PID returns multiple values(has multiple displays setup for the same PID), then this call will return the data for the first matching PID
   */
  float getScaleForPid(long pid) throws android.os.RemoteException;
  /** Translate a string that *might* be in translation file in the main Torque app. Do not call this method repeatedly for the same text! */
  String translate(String originalText) throws android.os.RemoteException;
  /**
   * Method to send PID data in raw form.
   *  Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header
   * @deprecated - see sendPIDDataV2 (which allows diagnostic headers to be set, or left as null)
   */
  @Deprecated
  boolean sendPIDData(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header) throws android.os.RemoteException;
  /**
   * Add / Update a PID from your plugin to the main app, Your PID is keyed by name.  This can be used to set a value on a PID in Torque.
   * 
   * @param value       The value to be shown in the display
   * @param stringValue A string value to be shown in the display - overrides 'value' when not null - note(try not to use numeric values here as they won't be unit converted for you).  Set to null to revert to using the 'float value'
   */
  boolean setPIDInformation(String name, String shortName, String unit, float max, float min, float value, String stringValue) throws android.os.RemoteException;
  /**
   * Get a list of all PIDs in torque, active or inactive.
   * 
   * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
   * @note (don't call this too often as it is computationally expensive!)
   */
  String[] listAllPIDs() throws android.os.RemoteException;
  /**
   * List all only the active PIDs
   * 
   * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
   * @note (don't call this too often as it is computationally expensive!)
   */
  String[] listActivePIDs() throws android.os.RemoteException;
  /**
   * List all only the active PIDs
   * 
   * The returned string is the ID of the PID to request over the aidl script.  To be helpful the first part of this string is the actual PID 'id'.
   * @note (don't call this too often as it is computationally expensive!)
   */
  String[] listECUSupportedPIDs() throws android.os.RemoteException;
  /**
   * Return the current value only for the PID (or PIDs if you pass more than one)
   * 
   * Use this method to frequently get the value of PID(s).  Be aware that the more PIDs you request, the slower the update speed (as the OBD2 adapter will require to update more PIDs and this is the choke-point)
   * This method is asynchronous updates via the adapter
   * 
   * @deprecated please use getPIDValuesAsDouble(...) instead
   */
  @Deprecated
  float[] getPIDValues(String[] pidsToRetrieve) throws android.os.RemoteException;
  /**
   * Return all the PID information in a single transaction. This is the new preferred method to get data from PIDs
   * 
   * The unit returned is the raw unit (used for getPIDValues) - you can get the users preferred unit by using the relevant API call
   * 
   * Format of returned string array:
   *  String[] {
   *       String "pid information in csv format"
   *       String "pid information in csv format"
   *       ...(etc)
   *    }
   * 
   *  Example:
   * 
   *   String[] {
   *      "<longName>,<shortName>,<unit>,<maxValue>,<minValue>,<scale>",
   *      "Intake Manifold Pressure,Intake,kPa,255,0,1",
   *      "Accelerator Pedal Position E,Accel(E),%,100,0,1"
   *   }
   */
  String[] getPIDInformation(String[] pidIDs) throws android.os.RemoteException;
  /**
   * Get the time that the PID was last updated.
   * 
   * @returns time when the PID value was retrieved via OBD/etc in milliseconds
   */
  long[] getPIDUpdateTime(String[] pidIDs) throws android.os.RemoteException;
  /**
   * Retrieves a list of PID values
   * 
   * @deprecated See getPIDInformation(...), listAllPIDs(), getPIDValues(...) and listActivePIDs() which replaces this call and can handle sensors with the same 'PID'
   */
  @Deprecated
  float[] getValueForPids(long[] pids) throws android.os.RemoteException;
  /**
   * Method to send PID data in raw form - this is for when pid plugin vendors want to protect their hard work when they have decyphed or licensed PIDs
   * Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header
   * @deprecated - see sendPIDDataPrivateV2 (which allows diagnostic headers to be set, or left as null)
   */
  @Deprecated
  boolean sendPIDDataPrivate(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header) throws android.os.RemoteException;
  /**
   * Cause Torque to stop communicating with the adpter so external plugins can take full control
   * 
   * This is when you want to do special things like reconfigure the adpater to talk to special units (ABS, SRS, Body control, etc)
   * which may require torque to stay away from asking information from the adpater
   * 
   * @return an int depicting the state of the lock
   * 
   *   0 = Lock gained OK - you now have exclusive access
   *  -1 = Lock failed due to unknown reason
   *  -2 = Lock failed due to another lock already being present (from your, or another plugin)
   *  -3 = Lock failed due to not being connected to adapter
   *  -4 = reserved - you still didn't get a lock.
   *  -5 = No permissions to access in this manner (enable the ticky-box in the plugin settings for 'full access')
   *  -6 = Lock failed due to timeout trying to get a lock (10 second limit hit)
   */
  int requestExclusiveLock(String pluginName) throws android.os.RemoteException;
  /**
   * Release the exclusive lock the plugin has with the adapter.
   * 
   * @param torqueMustReInitializeTheAdapter set this to TRUE if torque must reinit comms with the vehicle ECU to continue communicating - if it is set to FALSE, then torque will simply continue trying to talk to the ECU. Take special care to ensure the adpater is in the proper state to do this
   */
  boolean releaseExclusiveLock(String pluginName, boolean torqueMustReInitializeTheAdapter) throws android.os.RemoteException;
  /**
   * Method to send PID data in raw form.
   * This updated method allows diagnostic headers to be set if required (for special commands) or left as null.
   *  Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header,Start diagnostic command, Stop diagnostic command
   */
  boolean sendPIDDataV2(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header, String[] startDiagnostic, String[] stopDiagnostic) throws android.os.RemoteException;
  /**
   * Method to send PID data in raw form - this is for when pid plugin vendors want to protect their hard work when they have decyphed or licensed PIDs
   * This updated method allows diagnostic headers to be set if required (for special commands) or left as null.
   * Name, ShortName, ModeAndPID, Equation, Min, Max, Units, Header, Start diagnostic command, Stop diagnostic command
   */
  boolean sendPIDDataPrivateV2(String pluginName, String[] name, String[] shortName, String[] modeAndPID, String[] equation, float[] minValue, float[] maxValue, String[] units, String[] header, String[] startDiagnostic, String[] stopDiagnostic) throws android.os.RemoteException;
  /**
   * This retrieves the last PID that torque read from the adapter in RAW form (as the adapter sent it) 'NOT READY' may be returned
   * if the adapter has not yet retrieved that PID. The OBDCommand should be as sent to the adpater (no spaces - eg: '010D')
   * 
   * Despite the name, this cannot be used to send commands directly to the adapter, use the sendCommandGetResponse(...) method if
   * you require direct access, with the exclusive lock command if you will be sending several commands or changing the adapter mode
   */
  String[] getPIDRawResponse(String OBDCommand) throws android.os.RemoteException;
  /** Get the protocol being used (or 0 if not currently connected) - the protocol number is as per the ELM327 documentation */
  int getProtocolNumber() throws android.os.RemoteException;
  /** Get the protocol name being used (or AUTO if not currently connected) */
  String getProtocolName() throws android.os.RemoteException;
  /** Used by the dash tracker plugin, NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
  boolean setCurrentDashboard(String dashboardName, int hashKey) throws android.os.RemoteException;
  /**
   * Same as listAllPIDs except it returns detected sensors as well (these are generally not available as PIDs, but are sensors discovered in the course of talking to
   * the vehicle ECU (usually from other ECUs replying)
   */
  String[] listAllPIDsIncludingDetectedPIDs() throws android.os.RemoteException;
  /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
  String getSettingString(String setting, String def) throws android.os.RemoteException;
  /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
  boolean getSettingBoolean(String setting, boolean def) throws android.os.RemoteException;
  /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
  long getSettingLong(String setting, long def) throws android.os.RemoteException;
  /** Retrieve a setting from the app. NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
  int getSettingInt(String setting, int def) throws android.os.RemoteException;
  /** Get the theme type (day/night)  NON PUBLIC DO NOT USE SUBJECT TO CHANGE. */
  String getThemeType() throws android.os.RemoteException;
  /** Calibrate the accelerometer for the current device angle */
  boolean calibrateAccelerometer() throws android.os.RemoteException;
  /**
   * Get the current theme files as a URIs so we can get at it using content providers.
   * 
   * @param callingPackageName is the name of your package - eg 'my.someapp.stuff' so that uri permissions can be correctly granted
   */
  String[] getCurrentThemeUri(String callingPackageName) throws android.os.RemoteException;
  /**
   * Sends a list of themes (one per call) to Torque (which will remember them until it is quit or
   * restarted or sends out another THEME_QUERY broadcast)
   * 
   * Make sure your FileProvider for the file Uris is setup correctly, do not forget to grant read
   * only permission. If the user selects the theme then torque will pull the file uris and copy
   * to local (to the app) storage
   * 
   * returns true if successful, false if not.
   */
  boolean putThemeData(String packageName, String themeName, String description, String author, String thumbnailUri, String[] themeFileUris) throws android.os.RemoteException;
  /**
   * Return the current value only for the PID (or PIDs if you pass more than one)
   * 
   * Use this method to frequently get the value of PID(s).  Be aware that the more PIDs you request, the slower the update speed (as the OBD2 adapter will require to update more PIDs and this is the choke-point)
   * This method is asynchronous updates via the adapter
   */
  double[] getPIDValuesAsDouble(String[] pidsToRetrieve) throws android.os.RemoteException;
  /** Gets the current header in use (or null if no specific header has been set and the adapter default is being used) */
  String getCurrentHeader() throws android.os.RemoteException;
  /**
   * Utility method to ease the recombination of multiple CAN/etc frames from multiple ECUs
   * 
   * Basically put your response[] from sendCommandGetResponse() here and it will be combined from
   * several ECU responses into one coherent response (or more if more than one ECU responded).
   * Also handles out-of-order responses.
   */
  String[] recombineResponses(String[] rawResponsesFromECU) throws android.os.RemoteException;
  /** Returns true if Torque is using headers enabled (ATH1) with this connection */
  boolean areHeadersEnabled() throws android.os.RemoteException;
  /**
   * Captures frames directly using the AT MA command - useful for CANBUS systems
   * 
   * time - the time to wait whilst capturing in milliseconds. A value of 50ms should be enough
   * for every adapter type without causing the buffers to become full on fast systems.
   * 
   * returns an empty string if nothing was captured or the adapter was not in the correct state (still connecting to ECU, etc)
   */
  String[] captureFrames(int time) throws android.os.RemoteException;
}
