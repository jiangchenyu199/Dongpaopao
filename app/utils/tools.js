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

/**
 * 格式化消息时间为"HH:MM"
 * @param {string|Date} date - 日期字符串或Date对象
 * @returns {string} 格式化后的时间
 */
export const formatMessageTime = (date) => {
  if (!(date instanceof Date)) date = new Date(date);
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
};

/**
 * 格式化日期为"YYYY年MM月DD日 星期X"
 * @param {string|Date} date - 日期字符串或Date对象
 * @returns {string} 格式化后的日期
 */
export const formatDate = (date) => {
  if (!(date instanceof Date)) date = new Date(date);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const weekDay = weekDays[date.getDay()];
  return `${year}年${month}月${day}日 ${weekDay}`;
};

/**
 * 判断两个日期是否为同一天
 * @param {string|Date} date1 - 第一个日期
 * @param {string|Date} date2 - 第二个日期
 * @returns {boolean} 是否为同一天
 */
export const isSameDay = (date1, date2) => {
  if (!(date1 instanceof Date)) date1 = new Date(date1);
  if (!(date2 instanceof Date)) date2 = new Date(date2);
  return date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate();
};

