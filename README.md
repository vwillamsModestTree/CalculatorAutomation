# CalculatorAutomation
This is an appium test framework for the Windows Calculator application.

## Execution Prerequisities

1. Windows should be version 10 or greater
1. You should have the ability to enter admin mode
1. You should download the x86 version of `WinAppDriver.exe` (Some users experienced issues with the x64 version)
1. Install VS Code, which automatically installs a Windows Application Inspector that should be installed in this location: `C:\Program Files (x86)\Windows Kits\10\bin\10.0.19041.0\x64`
1. Appium should be setup.

## Appium Setup

Below is a guide that I used to install **Appium**.

1. Install `nvm` from: https://github.com/nvm-sh/nvm.
1. Install `node`. `npm` will also be installed at the same time.
1. Install **Appium** using `npm`.
1. Install **Appium** plugins using: `appium plugin install [plugin_name]`
1. Install drivers using : `appium driver install [driver_installation_key]`. Those installation keys can be found at: https://appium.github.io/appium/docs/en/2.0/ecosystem/#drivers.
1. Update drivers using: `appium driver update [name_of_driver]`. Eg. `appium driver update windows`

## Execution steps

1. Turn on Windows Developer mode.
2. Run Appium server with the command: `appium -a 127.0.0.1` or with plugins using: `appium -a 127.0.0.1 --use-plugins=[plugin_name]`
3. Run the following command in the project directory (the directory must contain a pom.xml file) `mvn clean install -Dtest=NameOfTestRunnerClass`

