[Conductor](https://github.com/bluelinelabs/Conductor) isn't good at handling view attaching or detaching, it delegates the task to `ControllerChangeHandler`. But `ControllerChangeHandler` doesn't do it well, some views which can't be seen are still attached.

## For Example

We have a `Route` with a `NormalController` as root. The `Controller`s under `NormalController` can't be seen.

|ControllerStack|Can be Seen|Attached|
|---------------|-----------|--------|
NormalController | yes | yes

Then we push a `DialogController`. The `Controller` under `DialogController` can be seen. So the `removeFromViewOnPush` must be set to `false`.

|ControllerStack|Can be Seen|Attached|
|---------------|-----------|--------|
DialogController | yes | yes
NormalController | yes | yes

Then we push another `NormalController`.

|ControllerStack|Can be Seen|Attached|
|---------------|-----------|--------|
NormalController | yes | yes
DialogController | no | no
NormalController | no | yes

The root `NormalController` can't be seen, but it's still attached now.

## Another Example

We have a `Route` with a `NormalController` as root.

|ControllerStack|Can be Seen|Attached|
|---------------|-----------|--------|
NormalController | yes | yes

Then we push a `SwipeToFinishController`. The `Controller` under `SwipeToFinishController` can be seen when the `SwipeToFinishController` is being swiping. So the `removeFromViewOnPush` must be set to `false`.

|ControllerStack|Can be Seen|Attached|
|---------------|-----------|--------|
SwipeToFinishController | yes | yes
NormalController | yes | yes

Then we push another `SwipeToFinishController`.

|ControllerStack|Can be Seen|Attached|
|---------------|-----------|--------|
SwipeToFinishController | yes | yes
SwipeToFinishController | yes | yes
NormalController | no | yes

Then we push another `SwipeToFinishController`.

|ControllerStack|Can be Seen|Attached|
|---------------|-----------|--------|
SwipeToFinishController | yes | yes
SwipeToFinishController | yes | yes
SwipeToFinishController | no | yes
NormalController | no | yes

That's worse. The `Controller` under the `SwipeToFinishController` can be seen only if this `SwipeToFinishController` is the top `Controller`. But all `Controller`s are attached.

## Principle

    Only keep the views which can be seen attached.

## Solution

1. Add a new property to `Controller`. The property describes how the `Controller` affects the visibility of `Controller`s below.
2. Let `Route` decide which views should be attached or detached according to the property of them.

In this project, I name the property `opacity`. It can be one of three values.

* `TRANSPARENT` - The Controller which is under this Controller can always be seen.
* `TRANSLUCENT` - The Controller which is under this Controller can only be seen if this Controller is the top Controller.
* `OPAQUE` - The Controller which is under current Controller will never be seen.

I do step 2 in a `Controller.LifecycleListener` named `ControllerAttacher`.

All `Controller`s need implement `ControllerOpacity` to tell `ControllerAttacher` its `opacity`.
