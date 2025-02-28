import { useCallback, useEffect, useState } from 'react';
import { Text, StyleSheet, SafeAreaView } from 'react-native';
import {
  Camera,
  useCameraDevice,
  useCameraPermission,
  useFrameProcessor,
  type Frame,
} from 'react-native-vision-camera';

export default function App(): React.ReactElement {
  const [position, setPosition] = useState<'front' | 'back'>('front');
  const device = useCameraDevice(position);
  const { hasPermission, requestPermission } = useCameraPermission();

  useEffect(() => {
    if (!hasPermission) {
      requestPermission();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const flipCamera = useCallback(() => {
    setPosition((p) => (p === 'back' ? 'front' : 'back'));
  }, []);

  const frameProcessor = useFrameProcessor((_: Frame) => {
    'worklet';

    console.log('Frame Processor');
  }, []);

  return (
    <SafeAreaView style={styles.container} onTouchEnd={flipCamera}>
      {!hasPermission && <Text style={styles.text}>No Camera Permission.</Text>}
      {hasPermission && device != null && (
        <Camera
          style={StyleSheet.absoluteFill}
          device={device}
          isActive={true}
          frameProcessor={frameProcessor}
        />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'black',
  },
  text: {
    color: 'white',
    fontSize: 20,
  },
});
