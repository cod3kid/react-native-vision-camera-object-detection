import { useMemo } from 'react';
import { createObjectDetectionPlugin } from './detectObjects';
import type { ObjectDetectionOptions, ObjectDetectionPlugin } from './types';

export function useObjectDetection(
  options?: ObjectDetectionOptions
): ObjectDetectionPlugin {
  return useMemo(() => createObjectDetectionPlugin(options), [options]);
}
