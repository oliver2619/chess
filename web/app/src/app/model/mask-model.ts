export class MaskModel {

  static countFields(mask: bigint): number {
    let ret = 0;
    const inv = ~mask;
    for (let m = (inv + 1n) & mask; m !== 0n; m = (inv + (m << 1n)) & mask) {
      ++ret;
    }
    return ret;
  }

  static fieldToMask(field: number): bigint {
    return 1n << BigInt(field);
  }

  static rowMask(row: number): bigint {
    return 255n << BigInt(8 * row);
  }

  static maskToField(mask: bigint): number {
    return mask.toString(2).length - 1;
  }

  static forEachField(mask: bigint, callback: (field: number) => void) {
    const inv = ~mask;
    for (let m = (inv + 1n) & mask; m !== 0n; m = (inv + (m << 1n)) & mask) {
      callback(this.maskToField(m));
    }
  }
}
