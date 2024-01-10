// 导入默认图片地址
import defaultUrl from '@/assets/images/luckyMoney.png'

/**
 * @description:动画属性
 * animation: name duration timing-function delay iteration-count direction;
 * animation-name    规定需要绑定到选择器的 keyframe 名称。
 * animation-duration    规定完成动画所花费的时间，以秒或毫秒计。
 * animation-timing-function    规定动画的速度曲线。
 * animation-delay    规定在动画开始之前的延迟。
 * animation-iteration-count    规定动画应该播放的次数。
 * animation-direction    规定是否应该轮流反向播放动画。
 * animation-fill-mode    规定动画在执行时间之外应用的值。
 *    none    不改变默认行为。
 *    forwards    当动画完成后，保持最后一个属性值（在最后一个关键帧中定义）。
 *    backwards    在 animation-delay 所指定的一段时间内，在动画显示之前，应用开始属性值（在第一个关键帧中定义）。
 *    both    向前和向后填充模式都被应用。
 * animation-play-state    规定动画是正在运行还是暂停。
 * */

interface LuckyMoneyData {
    // 图片地址
    url?: string;
    // 图片宽度
    width?: string;
    // 图片高度
    height?: string;
    //  插入的父元素
    parent?: HTMLElement;
    // 下落轨道
    orbit?: number[];
    // 动画设置
    animateSetting?: {
        keyframes: Keyframe[] ,
        options:KeyframeAnimationOptions
    };
    // 图片点击事件回调
    clickCallback?: () => void;
}

// 定义一个红包类
export default class LuckyMoney {
    // 红包图片背景     默认值：defaultUrl
    private readonly url: string;
    // 图片宽度        默认值：'20vw'
    private readonly width: string;
    // 图片高度        默认值：this.width ? 'auto' : '20vw'
    private readonly height: string;
    // 插入到哪个父元素中 默认值：document.body;
    private readonly parent: HTMLElement;
    // 为了避免红包重合，设计不同的轨道，共5个轨道 默认值： [0, 15, 35, 55, 75]
    private readonly orbit: number[]
    // 动画设置
    private readonly animateSetting: {
        keyframes: Keyframe[] ,
        options:KeyframeAnimationOptions
    };
    // img元素，用于销毁红包
    private img: HTMLImageElement = document.createElement('img');
    // 图片点击事件回调
    private readonly clickCallback: (() => void) | undefined;

    // 构造函数
    constructor(data: LuckyMoneyData) {
        data = data || {}
        this.url = data.url || defaultUrl;
        this.width = data.width || '20vw'
        this.height = data.height || this.width ? 'auto' : '20vw'
        this.parent = data.parent || document.body;
        this.orbit = data.orbit || [0, 15, 35, 55, 75]
        this.animateSetting = data.animateSetting || {
            // 关键帧
            keyframes: [
                {transform: `translate3d(0,-20vh,0) rotate(${15}deg)`, offset: 0},
                {transform: `translate3d(0,120vh,0) rotate(${60}deg)`, offset: 1}
            ],
            // 动画属性
            options: {
                // 动画持续时长：单位ms
                duration: (Math.random() * 5 + 1) * 1000,
                // 动画速度曲线
                easing: 'linear',
                //动画的速度曲线
                fill: 'forwards'
            }
        }
        this.clickCallback = data.clickCallback
        //     create
        this.create()
    }


    //  创建红包
    public create(): void {
        //     生成img标签
        this.img = document.createElement('img');
        //     设置img标签的src属性
        this.img.src = this.url
        // 设置图片定位
        this.img.style.position = 'absolute'
        // 设置图片的top值
        this.img.style.top = this.height
        // 为了避免红包重合，使用预定的轨道设置偏移量
        this.img.style.left = `${this.orbit[Math.round(Math.random() * (this.orbit.length - 1))]}vw`
        // 添加style，动画属性属性
        this.img.animate(this.animateSetting?.keyframes, this.animateSetting.options)
        // 设置img标签的宽高
        this.img.style.width = this.width
        this.img.style.height = this.height
        this.img.style.willChange = 'transform, opacity,display';
        // 图片点击事件
        this.img.onclick = this.click.bind(this)
        // 将img标签添加到parent中
        this.parent.appendChild(this.img)
        // 根据动画持续时长销毁红包
        this.destroyByTime(Number(this.animateSetting.options.duration))
    }

    // 销毁红包
    public destroy(): void {
        // 销毁img标签
        this.img.remove()
    }

    // 图片点击事件
    public click(): void {
        // 销毁图片
        this.destroy()
        // 执行回调函数
        this.clickCallback && this.clickCallback()
    }

    //     定时销毁
    public destroyByTime(second: number): void {
        //     销毁图片
        setTimeout(() => {
            this.destroy()
        }, second)
    }
}
