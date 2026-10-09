<template>
  <component
    :is="asChild ? 'slot' : 'button'"
    :class="buttonClasses"
    :disabled="disabled"
    @click="$emit('click', $event)"
  >
    <slot />
  </component>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  variant: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'primary', 'ghost', 'outline', 'destructive', 'link', 'secondary'].includes(v)
  },
  size: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'sm', 'lg', 'icon'].includes(v)
  },
  mode: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'icon', 'link', 'input'].includes(v)
  },
  shape: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'pill', 'circle'].includes(v)
  },
  disabled: {
    type: Boolean,
    default: false
  },
  asChild: {
    type: Boolean,
    default: false
  }
})

defineEmits(['click'])

const buttonClasses = computed(() => {
  // Base
  const base = 'inline-flex items-center justify-center whitespace-nowrap text-sm font-medium transition-all outline-offset-2 focus-visible:outline focus-visible:outline-2 focus-visible:outline-ring/70 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0'

  // Variants
  const variants = {
    default: 'bg-primary text-primary-foreground shadow-sm shadow-black/5 hover:bg-primary/90',
    primary: 'bg-primary text-primary-foreground shadow-sm shadow-black/5 hover:bg-primary/90',
    destructive: 'bg-destructive text-destructive-foreground shadow-sm shadow-black/5 hover:bg-destructive/90',
    outline: 'border border-input bg-background shadow-sm shadow-black/5 hover:bg-accent hover:text-accent-foreground',
    secondary: 'bg-secondary text-secondary-foreground shadow-sm shadow-black/5 hover:bg-secondary/80',
    ghost: 'hover:bg-accent hover:text-accent-foreground',
    link: 'text-primary underline-offset-4 hover:underline'
  }

  // Sizes
  const sizes = {
    default: 'h-9 px-4 py-2',
    sm: 'h-8 rounded-md px-3 text-xs',
    lg: 'h-10 rounded-md px-8',
    icon: 'h-9 w-9'
  }

  // Mode overrides
  const modes = {
    default: '',
    icon: 'h-9 w-9 p-0',
    link: 'p-0 h-auto',
    input: 'rounded-l-none border-l-0'
  }

  // Shape
  const shapes = {
    default: 'rounded-lg',
    pill: 'rounded-full',
    circle: 'rounded-full aspect-square'
  }

  return [
    base,
    variants[props.variant],
    props.mode !== 'default' ? modes[props.mode] : sizes[props.size],
    shapes[props.shape]
  ].filter(Boolean).join(' ')
})
</script>
