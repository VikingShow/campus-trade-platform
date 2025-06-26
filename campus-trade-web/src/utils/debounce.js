/**
 * 一个简单的防抖函数
 * @param {Function} func 要执行的函数
 * @param {number} delay 延迟时间 (毫秒)
 * @returns {Function} 返回一个新的防抖函数
 */
export function debounce(func, delay) {
  let timeout;
  return function(...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
      func.apply(this, args);
    }, delay);
  };
}