const plugIns = import.meta.glob("./*.ts");
// 引入plugIns下的所有文件
Object.keys(plugIns).forEach((key) => {
    if (key !== "./index.ts") {
        plugIns[key]();
    }
})