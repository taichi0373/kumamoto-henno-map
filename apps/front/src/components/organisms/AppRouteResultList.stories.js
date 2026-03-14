import AppRouteResultList from './AppRouteResultList.vue';

/** バス経路のサンプルデータ */
const busRoute = {
  duration: 28,
  startTime: '09:05',
  endTime: '09:33',
  totalFare: 350,
  totalDiscountFare: 300,
  transfers: 0,
  legs: [
    {
      mode: 'WALK',
      from: '熊本駅',
      to: '熊本駅前バス停',
      startTime: '09:05',
      endTime: '09:08',
      duration: 3,
      fare: null,
      discountFare: null,
      agencyName: null,
    },
    {
      mode: 'BUS',
      from: '熊本駅前バス停',
      to: '通町筋バス停',
      startTime: '09:10',
      endTime: '09:28',
      duration: 18,
      fare: 350,
      discountFare: 300,
      agencyName: '熊本電気鉄道',
    },
    {
      mode: 'WALK',
      from: '通町筋バス停',
      to: '熊本市役所',
      startTime: '09:28',
      endTime: '09:33',
      duration: 5,
      fare: null,
      discountFare: null,
      agencyName: null,
    },
  ],
};

/** 電車＋バスの乗り継ぎ経路のサンプルデータ */
const railAndBusRoute = {
  duration: 45,
  startTime: '10:15',
  endTime: '11:00',
  totalFare: 580,
  totalDiscountFare: null,
  transfers: 1,
  legs: [
    {
      mode: 'WALK',
      from: '新水前寺駅',
      to: '新水前寺駅ホーム',
      startTime: '10:15',
      endTime: '10:18',
      duration: 3,
      fare: null,
      discountFare: null,
      agencyName: null,
    },
    {
      mode: 'RAIL',
      from: '新水前寺駅',
      to: '熊本駅',
      startTime: '10:20',
      endTime: '10:38',
      duration: 18,
      fare: 230,
      discountFare: null,
      agencyName: 'JR九州',
    },
    {
      mode: 'BUS',
      from: '熊本駅前バス停',
      to: '益城熊本空港IC',
      startTime: '10:45',
      endTime: '10:58',
      duration: 13,
      fare: 350,
      discountFare: null,
      agencyName: '産交バス',
    },
    {
      mode: 'WALK',
      from: '益城熊本空港IC',
      to: '熊本空港',
      startTime: '10:58',
      endTime: '11:00',
      duration: 2,
      fare: null,
      discountFare: null,
      agencyName: null,
    },
  ],
};

/** 徒歩のみのサンプルデータ */
const walkRoute = {
  duration: 12,
  startTime: '14:00',
  endTime: '14:12',
  totalFare: 0,
  totalDiscountFare: null,
  transfers: 0,
  legs: [
    {
      mode: 'WALK',
      from: '熊本城',
      to: '桜の馬場 城彩苑',
      startTime: '14:00',
      endTime: '14:12',
      duration: 12,
      fare: null,
      discountFare: null,
      agencyName: null,
    },
  ],
};

export default {
  title: 'Design System/Organisms/AppRouteResultList',
  component: AppRouteResultList,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

/** 結果なし（初期状態） */
export const Empty = {
  args: {
    routes: [],
    activeRouteIndex: 0,
  },
  render: (args) => ({
    components: { AppRouteResultList },
    setup() { return { args }; },
    template: `
      <div style="width: 380px; min-height: 80px; border: 1px solid #dee2e6; border-radius: 8px; padding: 8px; background: #fafafa; display: flex; align-items: center; justify-content: center; color: #9ca3af; font-size: 14px;">
        <AppRouteResultList v-bind="args" />
        <span v-if="args.routes.length === 0">経路がありません</span>
      </div>
    `,
  }),
};

/** 1件の経路（バス） */
export const SingleRoute = {
  args: {
    routes: [busRoute],
    activeRouteIndex: 0,
  },
  render: (args) => ({
    components: { AppRouteResultList },
    setup() { return { args }; },
    template: `
      <div style="width: 380px;">
        <AppRouteResultList v-bind="args" />
      </div>
    `,
  }),
};

/** 複数経路（選択なし） */
export const MultipleRoutes = {
  args: {
    routes: [busRoute, railAndBusRoute, walkRoute],
    activeRouteIndex: 0,
  },
  render: (args) => ({
    components: { AppRouteResultList },
    setup() { return { args }; },
    template: `
      <div style="width: 380px;">
        <AppRouteResultList v-bind="args" />
      </div>
    `,
  }),
};

/** 2番目の経路がアクティブ */
export const SecondRouteActive = {
  args: {
    routes: [busRoute, railAndBusRoute, walkRoute],
    activeRouteIndex: 1,
  },
  render: (args) => ({
    components: { AppRouteResultList },
    setup() { return { args }; },
    template: `
      <div style="width: 380px;">
        <AppRouteResultList v-bind="args" />
      </div>
    `,
  }),
};

/** 割引運賃あり */
export const WithDiscountFare = {
  args: {
    routes: [busRoute],
    activeRouteIndex: 0,
  },
  render: (args) => ({
    components: { AppRouteResultList },
    setup() { return { args }; },
    template: `
      <div style="width: 380px;">
        <div style="margin-bottom: 8px; padding: 8px 12px; background: #e8f5e9; border-radius: 6px; font-size: 13px; color: #2e7d32;">
          ✅ 割引運賃が適用されています（350円 → 300円）
        </div>
        <AppRouteResultList v-bind="args" />
      </div>
    `,
  }),
};

/** サイドバー内での表示 */
export const InSidebar = {
  render: () => ({
    components: { AppRouteResultList },
    data() {
      return {
        routes: [busRoute, railAndBusRoute, walkRoute],
        activeIndex: 0,
      };
    },
    methods: {
      onSelectRoute(index) {
        this.activeIndex = index;
      },
    },
    template: `
      <div style="display: flex; height: 600px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="width: 380px; background: #fff; border-right: 1px solid #dee2e6; overflow-y: auto;">
          <div style="background: #e3f2fd; padding: 12px 16px; border-bottom: 1px solid #dee2e6; position: sticky; top: 0; z-index: 1;">
            <strong style="color: #1565c0;">経路探索結果（{{ routes.length }}件）</strong>
          </div>
          <AppRouteResultList
            :routes="routes"
            :active-route-index="activeIndex"
            @select-route="onSelectRoute"
          />
        </div>
        <div style="flex: 1; background: linear-gradient(135deg, #e8f5e9, #c8e6c9); display: flex; align-items: center; justify-content: center; flex-direction: column; color: #2e7d32;">
          <div style="font-size: 3rem;">🗺️</div>
          <div style="font-size: 14px; margin-top: 8px;">選択中の経路：{{ activeIndex + 1 }}番目</div>
        </div>
      </div>
    `,
  }),
};
