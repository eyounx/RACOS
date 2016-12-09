# RACOS
A theoretically-grounded derivative-free optimization method, born from a statistical view of evolutionary algorithms. More details can be found in the paper:
> Yang Yu, Hong Qian, and Yi-Qi Hu. Derivative-Free Optimization via Classification. In: Proceedings of the 30th AAAI Conference on Artificial Intelligence (AAAI'16), Phoenix, AZ, 2016  ([PDF file](http://lamda.nju.edu.cn/yuy/GetFile.aspx?File=papers/aaai16-racos.pdf))

Other downloadable sources: http://lamda.nju.edu.cn/code_RACOS.ashx and http://cs.nju.edu.cn/yuy/code_racos.ashx

# Sequential RACOS (SRACOS)
SRACOS is the online version of RACOS, which can be much faster than RACOS in online scenarios, where solutions have to be evaluated one after another. For example, on policy optimization in OpenAI Gym tasks with 2000 iterations, the experiment comparison is as the figure below, normalzied by the performance of SRACOS:
<table border=0><tr><td width="500px"><img src="https://github.com/eyounx/TMP/blob/master/RACOS/SRACOSexp.jpg?raw=true" alt="Expeirment results"/></td></tr></table>
Details can be found in the paper:
> Yi-Qi Hu, Hong Qian, and Yang Yu. Sequential classification-based optimization for direct policy search. In: Proceedings of the 31st AAAI Conference on Artificial Intelligence (AAAI’17), San Francisco, CA, 2017. ([PDF file](http://lamda.nju.edu.cn/yuy/GetFile.aspx?File=papers/aaai17-sracos-with-appendix.pdf))

SRACOS is currently available only in the Python version by running RACOS as
```Python
OnlineSwitch = true
racos.ContinueOpt(Sphere, SampleSize, MaxIteration, PositiveNum, RandProbability, UncertainBits, OnlineSwitch)
```

**A major rewrite of RACOS is coming soon...**

The codes are released under the GNU GPL 2.0 license. For commercial purposes, please contact Dr. Yang Yu (yuy@nju.edu.cn) or Prof. Zhi-Hua Zhou (zhouzh@nju.edu.cn)
