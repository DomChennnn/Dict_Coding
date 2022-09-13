# Dict_Coding

## 依赖
Java、Python3

> Apple M1 平台下需要 arm64 版本的 Java、Python3

## 运行测试

```
python3 Demo_CodingDict.py
```

## 代码规范

使用 black 格式化代码
https://github.com/psf/black

## 编码说明

编码分为有损编码和无损编码，均支持8bit/16bit/24bit/32bit图片

| 模式 | 图片位深 | 模式代号 | 备注说明                                                     | 是否完成 |
| ---- | -------- | -------- | ------------------------------------------------------------ | -------- |
| 有损 | 8        | 0000     | 单通道进行字典编码                                           | 否       |
| 有损 | 16       | 0001     | RGB565格式，转为RGB888，对三个通道分别字典编码               | 否       |
| 有损 | 24       | 0010     | RGB888格式，对三通道分别进行字典编码                         | 是       |
| 有损 | 32       | 0011     | RGBA8888格式，对RGB三通道进行字典编码，对A通道游程编码（RGB为有损，A为无损） | 是       |
| 无损 | 8        | 1000     | 单通道进行字典编码，并储存残差                               | 否       |
| 无损 | 16       | 1001     | RGB565格式，转为RGB888，对三个通道分别字典编码，并储存残差   | 否       |
| 无损 | 24       | 1010     | RGB888格式，对三通道分别进行字典编码，并储存残差             | 是       |
| 无损 | 32       | 1111     | RGBA8888格式，对RGB三通道进行字典编码，对A通道游程编码（RGB为有损，A为无损），对RGB储存残差 | 是       |



不同位深图片具体编码细节

| 编码细节           | 8bit                | 16bit               | 24bit               | 32bit               |
| ------------------ | ------------------- | ------------------- | ------------------- | ------------------- |
| 读图方式           | Image.open          | RGB565读图          | Image.open          | BmpAlphaImage       |
| 编码通道数         | 1                   | 3                   | 3                   | 3+1                 |
| 编码信息1          | mode,h,w            | mode,h,w            | mode,h,w            | mode,h,w            |
| 编码信息2          | length of 1 channel | length of 3 channel | length of 3 channel | length of 4 channel |
| 编码信息3          | some info for quant | some info for quant | some info for quant | some info for quant |
| 无损需要增加的信息 | Res                 | Res of RGB          | Res of RGB          | Res of RGB          |



8bit有损：

```
[mode(0), h, w]
[len(xc_encoded)]
[deldc]
```

8bit无损：

```
[mode(4), h, w]
[len(xc_encoded), len(res_encoded)]
[deldc]
```

16bit有损：

```
[mode(1), h, w]
[len(xc_encoded_R), len(xc_encoded_G), len(xc_encoded_B)]
[deldc_R, deldc_G, deldc_B]
```

16bit无损：

```
[mode(5), h, w]
[len(xc_encoded_R),len(xc_encoded_G),len(xc_encoded_B),len(res_encoded_R),len(res_encoded_G),len(res_encoded_B)]
[deldc_R, deldc_G, deldc_B]
```

24bit有损：

```
[mode(2), h, w]
[len(xc_encoded_R), len(xc_encoded_G), len(xc_encoded_B)]
[deldc_R, deldc_G, deldc_B]
```

24bit无损：

```
[mode(6), h, w]
[len(xc_encoded_R),len(xc_encoded_G),len(xc_encoded_B),len(res_encoded_R),len(res_encoded_G),len(res_encoded_B)]
[deldc_R, deldc_G, deldc_B]
```

32bit有损：

```
[mode(3), h, w]
[len(xc_encoded_R),len(xc_encoded_G),len(xc_encoded_B),len(xc_encoded_A),]
[deldc_R, deldc_G, deldc_B]
```

32bit无损：

```
[mode(7), h, w]
[len(xc_encoded_R),len(xc_encoded_G),len(xc_encoded_B),len(res_encoded_R),len(res_encoded_G),len(res_encoded_B),len(xc_encoded_A),]
[deldc_R, deldc_G, deldc_B]
```