import { VisionCameraProxy, type Frame } from 'react-native-vision-camera';
import type { ObjectDetectionPlugin, ObjectDetectionOptions } from './types';

const LINKING_ERROR = `Can't load plugin detectObjects. Try cleaning cache or reinstall plugin.`;

export function createObjectDetectionPlugin(
  options?: ObjectDetectionOptions
): ObjectDetectionPlugin {
  const plugin = VisionCameraProxy.initFrameProcessorPlugin('detectObjects', {
    ...options,
  });
  if (!plugin) {
    throw new Error(LINKING_ERROR);
  }
  return {
    detectObjects: (frame: Frame): string => {
      'worklet';
      // @ts-ignore
      return plugin.call(frame) as string;
    },
  };
}
