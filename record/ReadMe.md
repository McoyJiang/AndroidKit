## WavRecorder
WavRecorder is a customized AudioRecod which can record raw PCM data, and then add wav-head param.
Finally make .wav format audio files based on the input param of "outPath"

## How to use WavRecorder

### 1 create instance of WavRecorder

```
WavRecorder wavRecorder = new WavRecorder();
```

or you can create a customized WavRecorder, by passing specific param. the last parad determins that 
if the recorded audio will be cut by decibels at the beginning & end
```
WavRecorder wavRecorder = new WavRecorder(MIC, 44100, CHANNEL_IN_MONO, ENCODING_PCM_16BIT, true);
```

### 2 set the out path for expected audio file

```
wavRecorder.setOutputFile("your_out_path");
```

### 3 prepare and start audio record
```
wavRecorder.prepare();

wavRecorder.start();
```

## *Notice：before start WavRecorder, we should have granted all the permissions that this function need*
