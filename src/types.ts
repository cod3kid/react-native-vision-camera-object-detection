import type { Frame } from 'react-native-vision-camera';

export type ObjectDetectionOptions = {
  captureMode: 'image' | 'stream';
  detectionType: 'single' | 'multi';
  classifyObjects: boolean;
};

export type ObjectDetectionPlugin = {
  detectObjects: (frame: Frame) => string;
};
