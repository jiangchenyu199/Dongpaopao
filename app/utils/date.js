/**
 * 安全解析日期
 * @param {string|Date|undefined} val 接口返回的时间字符串或 Date
 * @returns {Date|null}
 */
export function parseDateSafe(val) {
	if (!val) return null;
	if (typeof val === 'string' && val.includes(' ') && !val.includes('T')) {
		val = val.replace(' ', 'T');
	}
	const d = new Date(val);
	return isNaN(d.getTime()) ? null : d;
}

/**
 * 可读的日期时间展示：yyyy-MM-dd HH:mm
 * @param {string|Date|undefined} val
 * @returns {string}
 */
export function formatTimeDisplay(val) {
	const date = parseDateSafe(val);
	if (!date) return (val && String(val)) || '';
	const y = date.getFullYear();
	const m = String(date.getMonth() + 1).padStart(2, '0');
	const d = String(date.getDate()).padStart(2, '0');
	const h = String(date.getHours()).padStart(2, '0');
	const min = String(date.getMinutes()).padStart(2, '0');
	return `${y}-${m}-${d} ${h}:${min}`;
}

/**
 * 简短格式：M/D HH:mm
 * @param {string|Date|undefined} timestamp
 * @returns {string}
 */
export function formatTime(timestamp) {
	const date = parseDateSafe(timestamp);
	if (!date) return '';
	return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
}
