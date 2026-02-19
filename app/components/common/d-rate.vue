<template>
	<view class="d-rate">
		<text
			v-for="i in count"
			:key="i"
			class="d-rate__star"
			:class="{ 'd-rate__star--disabled': disabled }"
			:style="getStarStyle(i)"
			@click="onStarClick(i)"
		>★</text>
	</view>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

const props = withDefaults(
	defineProps<{
		modelValue?: number;
		count?: number;
		disabled?: boolean;
		activeColor?: string;
		inactiveColor?: string;
		size?: number;
	}>(),
	{
		modelValue: 0,
		count: 5,
		disabled: false,
		activeColor: '#ff9900',
		inactiveColor: '#e5e7eb',
		size: 20
	}
);

const emit = defineEmits<{ (e: 'update:modelValue', value: number): void }>();

function getStarStyle(index: number) {
	const active = index <= props.modelValue;
	return {
		color: active ? props.activeColor : props.inactiveColor,
		fontSize: props.size + 'px'
	};
}

function onStarClick(index: number) {
	if (props.disabled) return;
	emit('update:modelValue', index);
}
</script>

<style scoped>
.d-rate {
	display: inline-flex;
	align-items: center;
	gap: 4rpx;
}

.d-rate__star {
	line-height: 1;
}

.d-rate__star--disabled {
	pointer-events: none;
}
</style>
