![alt text](https://github.com/UnitXOrg/adyo-android/blob/master/adyo_logo.png?raw=true "Adyo Android Logo")

# Adyo Android SDK

The Adyo Android SDK makes it easy to integrate Adyo ads into your Android app. We provide a pre-built UI element to easily serve ads out of the box. We also expose our APIs to provide you with more control on the serving experience.

## Requirements

- Android min SDK version 16

## Installation

### Gradle


Add this line to your app's `build.gradle` under `dependencies`:

```groovy
dependencies {
  compile 'za.co.adyo:adyoandroidsdk:1.1.7.12'
}
```

**Note**: It is **not** recommended that you use `compile 'za.co.adyo:adyoandroidsdk:+'` as full backwards compatibility can not be ensured in future versions.

## Usage

### Using the AdyoZoneView

The `AdyoZoneView` does all the hard work of displaying and handling the placement for you.

Add the view to your layout:
```xml 
<za.co.adyo.android.views.AdyoZoneView
        android:id="@+id/adyo_zone_view"
        style="@style/AdyoWebView"
        android:layout_width="match_parent"
        android:layout_height="200px" />
```

In your Activity/Fragment, fetch the view from your xml:

```java
AdyoZoneView adyoZoneView = findViewById(R.id.adyo_zone_view)
```

You can also programmatically create and add the `AdyoZoneView` with its default constructor. Please remember to add layout parameters to ensure your view is the right size.

```java 
AdyoZoneView adyoZoneView = new AdyoZoneView(MainActivity.this);
adyoZoneView.setLayoutParams(new ViewGroup.LayoutParams(320, 200));
```

Next create a list of parameters that we will use to request a placement:
```java
PlacementRequestParams params = new PlacementRequestParams(
                context,              // Context
                13,                   // Network Id (Required)
                1,                    // Zone Id (Required)
                "YPf7G7BXtFCdEn",     // User Id (Nullable)
                new String[0],        // List of Keywords (Nullable)
              new String[]{"image"} // List of supported creative types. Can be "image", "rich-media", "tag"(Nullable)
                320,                  // Width (Nullable)
                200,                  // Height (Nullable)
                new JSONObject());    // Custom Keyword (Nullable)
                
```
We will discuss the `PlacementRequestParams` in full shortly.

**Note:** Please note that if you set the `width` and `height` as `null`, the `AdyoZoneView` will use its set width and height as parameters.

To display a placement in your `AdyoZoneView`, you simply call the following method:
```java
adyoZoneView.requestPlacement(params);

```


This will request a placement from the server and display the placement's creative on response of the request.

#### Other built in functionality
The `AdyoZoneView` also does the following automatically:
- Records impressions on the given placement
- Records third party impressions if the placement provides a third party impression URL
- Records clicks on the given placement if the placement provides a click URL 

### Using the Adyo Helper

If you would like something a bit more custom you can use your own view together with our `Adyo` helper class. This helper class is provided to simplify placement requests.

For the purpose of this example we will use an Android WebView.

Fetch your `WebView` from your xml:
```java
WebView webView = findViewById(R.id.web_view);
```
Next create a list of parameters that we will use to request a placement:
```java
PlacementRequestParams params = new PlacementRequestParams(
                context,              // Context
                13,                   // Network Id (Required)
                1,                    // Zone Id (Required)
                "YPf7G7BXtFCdEn",     // User Id (Nullable)
                new String[0],        // List of Keywords (Nullable)
                new String[]{"image"} // List of supported creative types. Can be "image", "rich-media", "tag"(Nullable)
                320,                  // Width (Nullable)
                200,                  // Height (Nullable)
                new JSONObject());    // Custom Keyword (Nullable)
```
We will discuss the `PlacementRequestParams` in full shortly.

Create a `PlacementRequestListener`:

```java
PlacementRequestListener listener = new PlacementRequestListener() {
   @Override
    public void onRequestComplete(boolean isFound, final Placement placement) {

          if(isFound)
          {
              ... 
              //Everything will happen here
          }
    }

    @Override
    public void onRequestError(String error) {

    }
};
```
We will discuss the `PlacementRequestListener` in full shortly.

To request a placement you can use our `Adyo` helper class as follows:

```java
Adyo.requestPlacement(context, params, listener);

```

To record clicks you can use our `Adyo` helper class as follows:
```java
Adyo.recordClicks(context, placement);

```
To record impressions you can use our `Adyo` helper class as follows:
```java
Adyo.recordImpression(context, placement, impressionRequestListener);

```
We will discuss the `impressionRequestListener` in full shortly.

**Note:** If a third party impression URL exists for the placement, it will also be recorded.

**Note 2:** The Adyo analytics API automatically detects duplicate impression requests so you don't have to worry if you are calling the `recordImpression` method more than once (e.g When the third party impression URL request has failed and you would like to try again).

You will also need to handle the refreshing of placements yourself. You can use our `Adyo` helper class as follows:

```java
Adyo.refreshPlacement(context, placement, placementRequestParams, placementRequestListener);

```

## Class Details

Let's discuss each class in full.

### Adyo

This helper class is provided to simplify placement requests.

#### Adyo Methods

| Method | Description |
| --- | --- |
| `void requestPlacement(Context, PlacementRequestParams, PlacementRequestListener)` | Gets a single placement that fits the parameters' criteria.  |
| `void recordImpression(Context, Placement, @Nullable ImpressionRequestListener)` | GET request to  the placement's impression URL to record an impression. It will also record the third party impression if the placement contains such a URL. |
| `void recordClicks(Context, Placement)` | GET request to the placement's click URL if such a URL exists. Also determines how to display page based on api.|
| `void refreshPlacement(Context, Placement, PlacementRequestParams, PlacementRequestListener)` | Refreshes the accepted placement after the time specified by the placement's `refreshAfter` property.

**Note:** The Adyo analytics API automatically detects duplicate impression requests so you don't have to worry if you are calling the `recordImpression` method more than once (e.g When the third party impression URL request has failed and you would like to try again).

### PlacementRequestParams

A holder object containing all the parameters needed to make a `requestPlacement` call.

The parameters consist of the following:

| Name | Type | Description |
| --- | --- | --- |
| `networkId` | long | Network id corresponding to the placement. |
| `zoneId` | long | Zone id corresponding to the placement. |
| `userId` | Nullable String | Id of the current user. |
| `keywords` | Nullable String [] | A list of keywords associated with the placement. |
|`creativeType`| Nullable String []| List of supported creative types. Can be "image", "rich-media", and/or "tag".|
| `width` | Nullable Integer | Width of the zone. |
| `height` | Nullable Integer | Height of the zone. |
| `customKeywords` | Nullable JSONObject | Key value pairs of custom keywords. |

A `PlacementRequestParams` can be created as follows:

```java
 PlacementRequestParams params = new PlacementRequestParams(
                context,                   // Context              
                13,                        // Network Id (Required)
                1,                         // Zone Id (Required)
                "YPf7G7BXtFCdEn",          // User Id (Nullable)
                new String[0],             // List of Keywords (Nullable)
                new String[]{"image"}      // List of supported creative types. Can be "image", "rich-media", "tag"(Nullable)
                null,                      // Width (Nullable)
                200,                       // Height (Nullable)
                new JSONObject());         // Custom Keyword (Nullable)
```


**Note:** A shorthand constructor without width, height and keywords also exists.


### PlacementRequestListener

A callback used to catch the results of a `requestPlacement` call. 

The `onRequestComplete` will be called if the request returns from the server with a response.  This function has 2 parameters: 
- `isFound`:  A boolean indicating if a placement was found. 
- `placement`: The placement object found/ Null if not found.

The `onRequestError` will be called if the server could not be reached.
This function has 1 parameter: 
- `error `:  The error given by the server.

A `PlacementRequestListener` can be created as follows:

```java
 PlacementRequestListener listener = new PlacementRequestListener() {

      @Override
      public void onRequestComplete(boolean isFound, Placement placement) {

      }

      @Override
      public void onRequestError(String error) {

       }
};
```
### Placement

An object that models a placement and its properties.

#### Placement Properties

| Name | Type | Description |
| --- | --- | --- |
| `creativeUrl` | String | The URL of the ad that will be displayed in the view. |
| `htmlUrl` | String | The URL of the ad that will be displayed in the view when the creative is of type Rich Media. |
| `creativeType` | String | Describes the type of creative. Currently we support the following: `image`, `rich-media`, `tag` |
| `htmlDomain` | String | Used with the htmlUrl when the creative is of type Rich Media |
| `impressionURL` | String | The URL to be used to record an impression. |
| `clickURL` | Nullable String  | The URL to be used when the ad is clicked on. |
| `thirdPartyImpressionURL` | Nullable String | The URL to be used to record a third party impression. |
| `refreshAfter` | int | The time to wait before refreshing the placement in seconds. |
| `appTarget` | int | Determines how the click URL will be handled. Currently we support the following: `APP_TARGET_DEFAULT` = External browser, `APP_TARGET_INSIDE` = Inside app, `APP_TARGET_POPUP` = Popup. |
| `metadata` | JSONObject | Metadata sent through with the placement. |
| `width` | int | Width of the view. |
| `height` | int | Height of the view. |
| `matchedKeywords` | JSONArray | A list of keywords that specify which keywords from the request matched. |

### AdyoWebViewClient

A custom Android `WebViewClient` that has a `Placement` object property.

#### AdyoWebViewClient Methods

The `AdyoWebViewClient ` has all the properties and methods of a normal Android  `WebViewClient` with the following additions:

| Method | Description |
| --- | --- |
| `void setPlacement(Placement)` | Setter for the placement property.  |

### ImpressionRequestListener

A callback used to catch the results when an impression is being recorded. The `Adyo` helper class will record the Adyo impression and the third party impression if it exists in its `recordImpression` method.

The `onRequestComplete` will be called if just the impression request was called and was successful or both impression and third party impression requests were called and both were successful.  
This function has 2 parameters: 
- `impressionCode`:  HTTP status code for the `RecordImpressionRequest`. 
- `thirdPartyCode`: HTTP status code for the `RecordThirdPartyImpressionRequest`.

The `onRequestError` will be called if the server could not be reached, the impression request was called and was unsuccessful or both calls were made and at least one was unsuccessful.
This function has 2 parameters:
- `impressionError`: The error from the `RecordImpressionRequest`.
- `thirdPartyImpressionError`:  The error from the `RecordThirdPartyImpressionRequest`.
If one of the parameters is null it either means only that specific call was unsuccessful or in the case of  `thirdPartyImpressionError`, that the call was not made because no such URL exists.

A `ImpressionRequestListener` can be created as follows:

```java
ImpressionRequestListener listener = new ImpressionRequestListener() {
      @Override
      public void onRequestComplete(Integer impressionCode, Integer thirdPartyCode) {
                                   
      }
      @Override
      public void onRequestError(String impressionError, String thirdPartyImpressionError) {
                                  
      }
};
```

## Sample Project

A sample app has been included in the repo to show how the SDK is used. Download and run the app to see the SDK in action. 

The project also provides a testing tool for you to input custom parameters to test-drive your own ads.

## Feedback

If you have any feedback please feel free to email us at devops@unitx.co.za.





