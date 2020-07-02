package magicnut.godotplugin.consentgdpr;

import android.app.Activity;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

public class ConsentPlugin extends GodotPlugin {
    public static final String PLUGIN_NAME = "ConsentPlugin";
    ConsentInformation consentInformation;
    ConsentForm form;
    Activity activity;

    /**
     * Constructor calling super and setting the activity.
     *
     * @param godot The godot app that instantiates the plugin.
     */
    public ConsentPlugin(Godot godot) {
        super(godot);
        activity = godot;
        TestDevice testDevice = new TestDevice(activity);
        testDevice.setupTestDeviceIfDebugEnabled();
    }

    /**
     * Determines the status of a user's consent.
     *
     * @param publisherIds At least one publisher id from an admob account that has been verified.
     */
    public void requestConsentInformation(String[] publisherIds) {
        Log.d(PLUGIN_NAME, "requestConsentInfo called");
        consentInformation = ConsentInformation.getInstance(activity);

        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                Log.d(PLUGIN_NAME, "consent status update " + consentStatus.toString());
                //TODO maybe not use the ordinal?
                emitSignal("consent_info_updated", consentStatus.ordinal());
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                Log.d(PLUGIN_NAME, "Failed consent status update: " + errorDescription);
                emitSignal("failed_to_update_consent_information", errorDescription);
            }
        });
    }

    /**
     * Builds the google-rendered consent form with the supplied options.
     *
     * @param privacyUrl                   A URL to the apps privacy agreement.
     * @param withPersonalizedAdsOption    Display option to use personalized ads?
     * @param withNonPersonalizedAdsOption Display option to use only non personalized ads?
     * @param withAdFreeOption             Display form option to use the paid/ad free version of the app?
     */
    public void buildConsentForm(String privacyUrl, final boolean withPersonalizedAdsOption, final boolean withNonPersonalizedAdsOption, final boolean withAdFreeOption) {
        Log.d(PLUGIN_NAME, "buildConsentForm called");
        try {
            final URL finalUrl = createUrl(privacyUrl);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConsentForm.Builder builder = new ConsentForm.Builder(activity, finalUrl).withListener(
                            new ConsentFormListener() {
                                @Override
                                public void onConsentFormLoaded() {
                                    // Consent form loaded successfully.
                                    Log.d(PLUGIN_NAME, "consent form loaded");
                                    emitSignal("consent_form_loaded");
                                }

                                @Override
                                public void onConsentFormOpened() {
                                    // Consent form was displayed.
                                    Log.d(PLUGIN_NAME, "consent form opened");
                                    emitSignal("consent_form_opened");
                                }

                                @Override
                                public void onConsentFormClosed(
                                        ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                    // Consent form was closed.
                                    Log.d(PLUGIN_NAME, "consent form closed");
                                    //TODO maybe not use ordinal here
                                    emitSignal("consent_form_closed", consentStatus.ordinal(), userPrefersAdFree);
                                }

                                @Override
                                public void onConsentFormError(String errorDescription) {
                                    // Consent form error.
                                    Log.d(PLUGIN_NAME, "consent form error: " + errorDescription);
                                    emitSignal("consent_form_error", errorDescription);
                                }
                            });

                    if (withPersonalizedAdsOption) {
                        Log.d(PLUGIN_NAME, "Consent form built with personalized ads option.");
                        builder.withPersonalizedAdsOption();
                    }

                    if (withNonPersonalizedAdsOption) {
                        Log.d(PLUGIN_NAME, "Consent form built with non personalized ads option.");
                        builder.withNonPersonalizedAdsOption();
                    }

                    if (withAdFreeOption) {
                        Log.d(PLUGIN_NAME, "Consent form built with ad free option.");
                        builder.withAdFreeOption();
                    }

                    form = builder.build();
                }
            });
        } catch (Exception e) {
            Log.d(PLUGIN_NAME, e.getLocalizedMessage());
        }
    }

    /**
     * Use this method to load the consent form after it has been build.
     */
    public void loadConsentForm() {
        Log.d(PLUGIN_NAME, "LoadConsentForm called");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                form.load();
            }
        });
    }

    /**
     * Show the consent form after it has been loaded.
     */
    public void showConsentForm() {
        Log.d(PLUGIN_NAME, "showConsentForm called");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                form.show();
            }
        });
    }

    /**
     * Turns a privacy url string into its URL class representation
     *
     * @param privacyUrl A string to your privacy url.
     * @return The url as Java class
     */
    private URL createUrl(String privacyUrl) {
        URL url = null;
        try {
            url = new URL(privacyUrl);
        } catch (MalformedURLException e) {
            emitSignal("malformed_privacy_url", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return url;
    }

    @NonNull
    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    /**
     * Return all the method names as list that can be called from godot side.
     *
     * @return
     */
    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                "requestConsentInformation",
                "buildConsentForm",
                "loadConsentForm",
                "showConsentForm");
    }

    /**
     * A set of all signals the plugin can emit.
     *
     * @return
     */
    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("consent_info_updated", Integer.class));
        signals.add(new SignalInfo("failed_to_update_consent_information", String.class));
        signals.add(new SignalInfo("consent_form_loaded"));
        signals.add(new SignalInfo("consent_form_opened"));
        signals.add(new SignalInfo("consent_form_closed", Integer.class, Boolean.class));
        signals.add(new SignalInfo("consent_form_error", String.class));
        signals.add(new SignalInfo("malformed_privacy_url", String.class));

        return signals;
    }
}
