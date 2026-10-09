<template>
  <nav v-if="totalPages > 1" class="mx-auto flex w-full justify-center" aria-label="pagination">
    <div class="flex flex-row items-center gap-1">
      <!-- Previous -->
      <Button
        variant="ghost"
        mode="icon"
        shape="default"
        :disabled="currentPage <= 1"
        @click="$emit('change', currentPage - 1)"
        aria-label="上一页"
      >
        <svg class="h-4 w-4 rtl:rotate-180" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </Button>

      <!-- Page numbers -->
      <template v-for="item in displayItems" :key="item">
        <span
          v-if="item === 'ellipsis'"
          class="flex h-9 w-9 items-center justify-center"
        >
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h.01M12 12h.01M19 12h.01" />
          </svg>
          <span class="sr-only">...</span>
        </span>
        <Button
          v-else
          :variant="item === currentPage ? 'outline' : 'ghost'"
          mode="icon"
          shape="default"
          :class="item === currentPage ? 'pointer-events-none' : ''"
          @click="$emit('change', item)"
          :aria-current="item === currentPage ? 'page' : undefined"
        >
          {{ item }}
        </Button>
      </template>

      <!-- Next -->
      <Button
        variant="ghost"
        mode="icon"
        shape="default"
        :disabled="currentPage >= totalPages"
        @click="$emit('change', currentPage + 1)"
        aria-label="下一页"
      >
        <svg class="h-4 w-4 rtl:rotate-180" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </Button>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import Button from './Button.vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  siblingCount: {
    type: Number,
    default: 1
  }
})

defineEmits(['change'])

const displayItems = computed(() => {
  const total = props.totalPages
  const current = props.currentPage
  const siblings = props.siblingCount

  // If total pages is small enough, show all
  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i + 1)
  }

  const items = []
  const leftSibling = Math.max(current - siblings, 1)
  const rightSibling = Math.min(current + siblings, total)

  const showLeftEllipsis = leftSibling > 2
  const showRightEllipsis = rightSibling < total - 1

  // Always show first page
  items.push(1)

  if (showLeftEllipsis) {
    items.push('ellipsis')
  } else {
    for (let i = 2; i < leftSibling; i++) {
      items.push(i)
    }
  }

  // Siblings + current
  for (let i = leftSibling; i <= rightSibling; i++) {
    if (i !== 1 && i !== total) {
      items.push(i)
    }
  }

  if (showRightEllipsis) {
    items.push('ellipsis')
  } else {
    for (let i = rightSibling + 1; i < total; i++) {
      items.push(i)
    }
  }

  // Always show last page
  items.push(total)

  return items
})
</script>
