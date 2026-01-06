/**
 * 时间格式化函数
 * @param {string|Date} time - 时间字符串或Date对象
 * @returns {string} 格式化后的时间
 */
export const formatTime = (time) => {
  if (!time) return '';
  
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const minutes = Math.floor(diff / (1000 * 60));
  
  if (minutes < 1) {
    return '刚刚';
  } else if (hours < 1) {
    return `${minutes}分钟前`;
  } else if (days === 0) {
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  } else if (days === 1) {
    return '昨天';
  } else if (days < 7) {
    return `${days}天前`;
  } else {
    return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
  }
};

