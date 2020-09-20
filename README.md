# trame_app
Track Me App

* Overview:
  - Tracking location of user
  - Recording the road that user went
  
* Technical:
  - Architecture: MVVM
      + data        : contains classes which represents an object or object state but it isn't entity
      + db          : constains AppDB and entities(tables)
      + extension   : constains AdapterBinding and class provide its defined methods
      + helper      : contains class which provide popular methods
      + repo        : contains repositories which provide methods for View Model or Service
      + service     : cosntains services
      + ui          : constains activities, fragmens
  - Language: Kotlin 
  - Important Dependencies: 
      + room
      + lifecycle-viewmodel
      + rxjava, rxAndroid
      + stetho
      +. easypermissions
      
 
