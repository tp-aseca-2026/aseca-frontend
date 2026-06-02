export function byTestTag(testTag: string) {
    return $(`android=new UiSelector().resourceId("${testTag}")`);
}