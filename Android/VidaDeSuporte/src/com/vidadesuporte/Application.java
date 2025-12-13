package com.vidadesuporte;

import com.parse.Parse;
import com.parse.PushService;

public class Application extends android.app.Application {

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();
	  Parse.initialize(this, "", "");
	  PushService.setDefaultPushCallback(this, com.vidadesuporte.activity.SplashScreen.class);
  }
}
