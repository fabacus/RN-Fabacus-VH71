
# react-native-fabacus-vh71

## Getting started

`$ npm install react-native-fabacus-vh71 --save`

### Mostly automatic installation

`$ react-native link react-native-fabacus-vh71`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-fabacus-vh71` and add `RNFabacusVh71.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNFabacusVh71.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.fabacus.vh71.RNFabacusVh71Package;` to the imports at the top of the file
  - Add `new RNFabacusVh71Package()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-fabacus-vh71'
  	project(':react-native-fabacus-vh71').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-fabacus-vh71/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-fabacus-vh71')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNFabacusVh71.sln` in `node_modules/react-native-fabacus-vh71/windows/RNFabacusVh71.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Cl.Json.RNFabacusVh71;` to the usings at the top of the file
  - Add `new RNFabacusVh71Package()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNFabacusVh71 from 'react-native-fabacus-vh71';

// TODO: What do with the module?
RNFabacusVh71;
```
  