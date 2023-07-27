## ALU.DIV

shang chushu yushu

判断是否chushu为0

符号拓展在yushu（根据shang的0位）

除数与被除数不同号，则将余数取为相反数

判断是否够   判断余数、除数是否同号

同号相减，异号相加 不够上0 够上1 yushu改为减后的

如果被除数为负，本质上算出来的是商的绝对值，  所以 shang 取反加一

被除数为负 算出来的yushu 为负
                 * 如果 yushu 和 chushu 相等 则xiangdeng == true
                                  * 如果 yushu 和 chushu 相反 则xiangfan == true
   * a 是 yushu 取反加一 是相反数
   * zhengchu是xiangdeng或者xiangfan





#### yushu和beichushu不同号

beichushu为-  整除

```
* 被除数为负 并且整除
* 余数直接返回 0
* 商应该返回 原商加一
```

不整除

直接返回

#### yushu和beichushu同号

beichushu为+  	商取反加一

