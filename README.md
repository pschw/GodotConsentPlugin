
# Consent plugin for Godot 3.2.2

This plugin implements the [Google Consent SDK](https://developers.google.com/admob/android/eu-consent/). The [GDPR](https://gdpr.eu/) requires Google App developers to obtain user consent before displaying any ads served by AdMob or its ad technology providers.

The main features of the plugin are:
* Verifying that consent for personalized or non-personalized ads has been obtained
* Detecting when consent has to be obtained again because the list of ad technology providers changed
* Displaying a customizable form to obtain consent

Support my work:
**Follow me on [Twitter](https://twitter.com/pascalschwenke) and add me on [LinkedIn](https://www.linkedin.com/public-profile/settings?trk=d_flagship3_profile_self_view_public_profile)!**

See the plugin in action:
[![Demo video](http://example.com/exampl.png)](http://example.com/link "Demo video")

## Adding the plugin to Godot 3.2.2
1. Follow the [official documentation](https://docs.godotengine.org/en/latest/getting_started/workflow/export/android_custom_build.html) to configure, install and enable an Android Custom Build.
2. Download the consent plugin from the release tab.
3. Extract the contents of ConsentPlugin.7zip to `res://android/plugins`
4. Additionally you will need another plugin for serving ads via AdMob. I have used [Shin-NiL's Godot-Android-Admob-Plugin](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin) in the past.
5. Call the consent plugin from a godot script (see chapter below).
6. When exporting your game via a preset in `Project>Export...` make sure that the `Use Custom Build` and `Consent Plugin` is checked.

## Calling the consent plugin in a godot script
Start by checking if the singleton instance of ConsentPlugin is available :
```javascript
if Engine.has_singleton("ConsentPlugin"):
    var singleton = Engine.get_singleton("ConsentPlugin")
```
to be continued...