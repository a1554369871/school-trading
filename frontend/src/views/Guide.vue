<template>
  <div class="min-h-screen bg-[#F8FAFB]">
    <!-- 顶部 Hero -->
    <section class="relative overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900"></div>
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-10 left-10 w-72 h-72 bg-white/10 rounded-full blur-3xl"></div>
        <div class="absolute bottom-10 right-10 w-96 h-96 bg-white/10 rounded-full blur-3xl"></div>
      </div>
      <div class="relative max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-20 text-center">
        <h1 class="text-4xl md:text-5xl font-bold text-white mb-4 tracking-tight">使用指南</h1>
        <p class="text-lg text-gray-300 max-w-2xl mx-auto">三步轻松完成闲置交易，让闲置物品找到新主人</p>
      </div>
    </section>

    <!-- 步骤卡片 -->
    <section class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 -mt-8 relative z-10">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Step 1 -->
        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center group hover:shadow-md transition-all duration-300">
          <div class="w-14 h-14 rounded-2xl bg-gray-900 flex items-center justify-center mx-auto mb-5">
            <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </div>
          <div class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-gray-100 text-gray-500 text-xs font-bold mb-3">1</div>
          <h3 class="text-lg font-bold text-gray-900 mb-2">拍照发布</h3>
          <p class="text-sm text-gray-500 leading-relaxed">拍下闲置物品的照片，填写描述和价格，一键发布到平台</p>
        </div>

        <!-- Step 2 -->
        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center group hover:shadow-md transition-all duration-300">
          <div class="w-14 h-14 rounded-2xl bg-gray-900 flex items-center justify-center mx-auto mb-5">
            <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
          </div>
          <div class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-gray-100 text-gray-500 text-xs font-bold mb-3">2</div>
          <h3 class="text-lg font-bold text-gray-900 mb-2">在线沟通</h3>
          <p class="text-sm text-gray-500 leading-relaxed">与买家或卖家实时聊天，协商价格和交易细节</p>
        </div>

        <!-- Step 3 -->
        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center group hover:shadow-md transition-all duration-300">
          <div class="w-14 h-14 rounded-2xl bg-gray-900 flex items-center justify-center mx-auto mb-5">
            <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
          </div>
          <div class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-gray-100 text-gray-500 text-xs font-bold mb-3">3</div>
          <h3 class="text-lg font-bold text-gray-900 mb-2">线下交易</h3>
          <p class="text-sm text-gray-500 leading-relaxed">约在校内安全地点当面交易，省心又放心</p>
        </div>
      </div>
    </section>

    <!-- 详细指南 -->
    <section class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <!-- 卖家指南 -->
      <div class="mb-12">
        <div class="flex items-center gap-3 mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-900 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </div>
          <h2 class="text-2xl font-bold text-gray-900">卖家指南</h2>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">1</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">注册并完善资料</h3>
              <p class="text-sm text-gray-500 leading-relaxed">注册账号后，完善个人资料和头像，增加买家信任度</p>
            </div>
          </div>
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">2</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">发布商品</h3>
              <p class="text-sm text-gray-500 leading-relaxed">上传清晰实物照片，填写详细描述、成色和合理价格</p>
            </div>
          </div>
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">3</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">回复买家咨询</h3>
              <p class="text-sm text-gray-500 leading-relaxed">及时回复买家的私信和评论，促成交易</p>
            </div>
          </div>
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">4</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">确认交易</h3>
              <p class="text-sm text-gray-500 leading-relaxed">收到订单后按约定方式完成交付，等待买家确认收货</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 买家指南 -->
      <div>
        <div class="flex items-center gap-3 mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-900 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
          <h2 class="text-2xl font-bold text-gray-900">买家指南</h2>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">1</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">浏览与搜索</h3>
              <p class="text-sm text-gray-500 leading-relaxed">通过分类或关键词搜索找到心仪的二手商品</p>
            </div>
          </div>
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">2</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">了解商品详情</h3>
              <p class="text-sm text-gray-500 leading-relaxed">查看商品图片、描述、成色，确认是否符合需求</p>
            </div>
          </div>
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">3</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">私聊卖家</h3>
              <p class="text-sm text-gray-500 leading-relaxed">通过私信与卖家沟通，了解商品细节和交易方式</p>
            </div>
          </div>
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex gap-4">
            <div class="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center shrink-0 mt-0.5">
              <span class="text-sm font-bold text-gray-500">4</span>
            </div>
            <div>
              <h3 class="text-sm font-bold text-gray-900 mb-1">下单与收货</h3>
              <p class="text-sm text-gray-500 leading-relaxed">选择取货方式下单，交易完成后确认收货并评价</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 安全提示 -->
    <section class="bg-white border-t border-gray-100">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div class="flex items-center gap-3 mb-6">
          <div class="w-10 h-10 rounded-xl bg-gray-900 flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
          </div>
          <h2 class="text-2xl font-bold text-gray-900">安全提示</h2>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="bg-gray-50 rounded-2xl p-6">
            <h3 class="text-sm font-bold text-gray-900 mb-2">校内交易</h3>
            <p class="text-sm text-gray-500 leading-relaxed">建议在校内公共场所当面交易，避免独自前往偏僻地点</p>
          </div>
          <div class="bg-gray-50 rounded-2xl p-6">
            <h3 class="text-sm font-bold text-gray-900 mb-2">验货付款</h3>
            <p class="text-sm text-gray-500 leading-relaxed">当面验货确认无误后再付款，避免先款后货的风险</p>
          </div>
          <div class="bg-gray-50 rounded-2xl p-6">
            <h3 class="text-sm font-bold text-gray-900 mb-2">保留凭证</h3>
            <p class="text-sm text-gray-500 leading-relaxed">保留聊天记录和交易凭证，遇到问题可作为维权依据</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
