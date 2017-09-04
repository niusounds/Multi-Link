# VR-MultiView-UDP

Simultaneous video playback system for Gear VR. It uses UDP broadcast packet to control playback state. Highly efficient to use with large local network.

**This project is not yet production ready.**

## Required

Node.js (Confirmed with v8.2.1 or later)


## Setup

* Put osig file(s) into app/src/main/assets
* Copy 360 video file(s) into Galaxy device(s). Directory is arbitrary. If you would like to use multiple devices, video files must be in same paths in all devices.
* Prepare controller with these commands

```
cd controller
npm install
```

## Launch

* To launch viewer in Gear VR, build `app` module and run
* To launch controller, run `npm start` command in `controller` directory.
