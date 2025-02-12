/** Exported memory */
export declare const memory: WebAssembly.Memory;
/**
 * assembly/index/add
 * @param a `i32`
 * @param b `i32`
 * @returns `i32`
 */
export declare function add(a: number, b: number): number;
/**
 * assembly/index/init
 * @param inp `~lib/string/String`
 * @returns `~lib/string/String`
 */
export declare function init(inp: string): string;
