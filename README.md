
Support my work:
**Follow me on [Twitter](https://twitter.com/pascalschwenke) and add me on [LinkedIn](https://www.linkedin.com/public-profile/settings?trk=d_flagship3_profile_self_view_public_profile)!**


# Consent plugin for Godot 3.2.2

This plugin implements the [Google Consent SDK](https://developers.google.com/admob/android/eu-consent/) for [Godot 3.2.2](https://godotengine.org/). The [GDPR](https://gdpr.eu/) requires Google App developers to obtain user consent before displaying any ads served by AdMob or its ad technology providers.

The main features of the plugin are:
* Verifying that consent for personalized or non-personalized ads has been obtained
* Detecting when consent has to be obtained again because the list of ad technology providers changed
* Displaying a customizable form to obtain consent


See the plugin in action:
[![Demo video](http://example.com/exampl.png)](http://example.com/link "Demo video")

## Adding the plugin to Godot 3.2.2
1. Follow the [official documentation](https://docs.godotengine.org/en/latest/getting_started/workflow/export/android_custom_build.html) to configure, install and enable an Android Custom Build.
2. Download the consent plugin from the release tab.
3. Extract the contents of ConsentPlugin.7z to `res://android/plugins`
4. Additionally you will need another plugin for serving ads via AdMob. I have used [Shin-NiL's Godot-Android-Admob-Plugin](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin).
5. Call the consent plugin from a godot script (see chapter below).
6. When exporting your game via a preset in `Project>Export...` make sure that the `Use Custom Build` and `Consent Plugin` is checked.

## Using the consent plugin in a godot script
Check if the singleton instance of `ConsentPlugin` is available. Then connect the signals of the plugin.
```javascript
func check_consent():
   if Engine.has_singleton("ConsentPlugin"):
      consent = Engine.get_singleton("ConsentPlugin")
      # connect signals
      consent.connect("consent_info_updated",self,"consent_info_updated")
      consent.connect("failed_to_update_consent_information",self,"failed_to_update_consent_information")
      consent.connect("consent_form_loaded",self,"consent_form_loaded")
      consent.connect("consent_form_opened",self,"consent_form_opened")
      consent.connect("consent_form_closed",self,"consent_form_closed")
      consent.connect("consent_form_error",self,"consent_form_error")
```
The Google Consent SDK requires that the consent status is initialized/updated:
```javascript
      #Pass your own publisher ids as a string array to the plugin
      var publisherIds = ["pub-1234567890123456"]
      consent.requestConsentInformation(publisherIds)
```
In case of success the plugin emits the signal `consent_info_updated`. Failure to request the consent information makes the plugin emit the signal `failed_to_update_consent_information`.
```javascript
func consent_info_updated(consent_status):
   # UNKNOWN
   if consent_status == 0:
      # Obtain user consent by showing a form
      obtain_consent()
   # NON_PERSONALIZED
   elif consent_status == 1:
      #configure AdMob implementation for non personalized apps
      configure_admob_non_personalized()
   # PERSONALIZED
   elif consent_status == 2:
      #configure AdMob implementation for personalized apps
      configure_admob_personalized()

func failed_to_update_consent_information(error_description):
   print(error_description)
```

To display the consent form you need to build, load and show the form in that order.
Building the form offers four options that are explained in the snippet below. Do not show the form unless it has been loaded successfully as indicated by the signal `consent_form_loaded`.
```javascript
func obtain_consent():
   # Replace this string value with your own privacy policy url
   var privacy_url = "https://www.yourcompan.com/your-privacy-policy/"

   # Add the choice to select personalized ads to the form
   var with_personalized_ads_option = true

   # Add the choice to select non personalized ads to the form
   var with_non_personalized_ads_option = true

   # Add the choice to pay for an ad free version, handled in consent_form_closed
   var with_ad_free_option = true

   consent.buildConsentForm(privacy_url, with_personalized_ads_option , with_non_personalized_ads_option , ad_free_option)
   consent.loadConsentForm()

func consent_form_loaded():
   consent.showConsentForm()

func consent_form_opened():
   pass

func consent_form_closed(consent_status, user_prefers_ad_free):
   # Handle consent_status: UNKNOWN = 0 NON_PERSONALIZED = 1 PERSONALIZED = 2
   # and/or the user's choice to pay for an ad free version

func consent_form_error(error_description):
   print(error_description)
```

### Remarks
Remember you are not done yet! You still need to send the users decision regarding consent to AdMob. How you do this depends on how you implement AdMob.
The Plugin can be debugged by using the Android Debug Bridge and the tag filter `ConsentPlugin`.