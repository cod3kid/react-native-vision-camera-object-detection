import type { Frame } from 'react-native-vision-camera';

export type ObjectDetectionOptions = {
  mode: 'single' | 'multi';
};

export type ObjectDetectionPlugin = {
  detectObjects: (frame: Frame) => string;
};
