import { defineStore } from 'pinia'
import {
  getMyConsumptionRecords,
  getMyGrowthRecords,
  getMyPointsRecords,
  getMyPointsTotal,
  getUserProfile,
  listMemberBenefits,
  updateUserProfile
} from '../api/member'

const levelNames = {
  normal: '普通会员',
  regular: '普通会员',
  silver: '白银会员',
  gold: '黄金会员',
  platinum: '铂金会员'
}

function normalizeLevels(levels) {
  if (Array.isArray(levels)) return levels
  if (!levels) return ['regular']
  try {
    const parsed = JSON.parse(levels)
    return Array.isArray(parsed) ? parsed : String(levels).split(',')
  } catch {
    return String(levels).split(',').map((item) => item.trim()).filter(Boolean)
  }
}

export const useMemberStore = defineStore('member', {
  state: () => ({
    memberInfo: {
      id: null,
      userId: null,
      level: '普通会员',
      levelCode: 'regular',
      joinDate: '',
      expireDate: '',
      growthValue: 0,
      totalSpend: 0,
      points: 0,
      inviteCode: '',
      inviteCount: 0,
      phone: '',
      birthday: '',
      province: '',
      city: '',
      district: '',
      detailAddress: '',
      bio: '',
      gender: null
    },
    privileges: [],
    growthHistory: [],
    consumptionHistory: [],
    pointsHistory: [],
    loading: false
  }),

  getters: {
    remainingDays(state) {
      if (!state.memberInfo.expireDate) return 0
      const expire = new Date(state.memberInfo.expireDate)
      if (Number.isNaN(expire.getTime())) return 0
      return Math.max(0, Math.ceil((expire - new Date()) / 86400000))
    },
    currentLevelPrivileges(state) {
      return state.privileges.filter((item) => normalizeLevels(item.levels).includes(state.memberInfo.levelCode))
    },
    nextLevelGrowth(state) {
      const value = state.memberInfo.growthValue || 0
      if (value < 500) return 500
      if (value < 1500) return 1500
      if (value < 4000) return 4000
      return value + 1000
    }
  },

  actions: {
    async fetchMemberInfo() {
      this.loading = true
      try {
        const res = await getUserProfile()
        const p = res.data || {}
        const levelCode = p.memberLevel || 'regular'
        this.memberInfo = {
          ...this.memberInfo,
          id: p.id,
          userId: p.userId,
          level: levelNames[levelCode] || levelNames.regular,
          levelCode,
          joinDate: p.createTime || '',
          expireDate: p.memberExpireTime || '',
          growthValue: p.growthValue || 0,
          totalSpend: Number(p.totalSpend || 0),
          points: p.points || 0,
          inviteCode: p.inviteCode || '',
          inviteCount: p.inviteCount || 0,
          phone: p.phone || '',
          birthday: p.birthday || '',
          province: p.province || '',
          city: p.city || '',
          district: p.district || '',
          detailAddress: p.detailAddress || '',
          bio: p.bio || '',
          gender: p.gender
        }
      } finally {
        this.loading = false
      }
    },

    async fetchPrivileges() {
      const res = await listMemberBenefits()
      this.privileges = (res.data || []).map((item) => ({
        ...item,
        levelList: normalizeLevels(item.levels)
      }))
    },

    async fetchGrowthHistory() {
      const res = await getMyGrowthRecords()
      this.growthHistory = (res.data || []).map((r) => ({
        id: r.id,
        date: r.createTime ? String(r.createTime).slice(0, 10) : '',
        value: r.value || 0,
        reason: r.reason || ''
      }))
    },

    async fetchConsumptionHistory() {
      const res = await getMyConsumptionRecords({ current: 1, pageSize: 20 })
      this.consumptionHistory = (res.data?.records || []).map((r) => ({
        id: r.id,
        date: r.createTime ? String(r.createTime).slice(0, 10) : '',
        amount: Number(r.amount || 0),
        description: r.description || '',
        type: r.type || ''
      }))
    },

    async fetchPointsHistory() {
      const res = await getMyPointsRecords({ current: 1, pageSize: 20 })
      this.pointsHistory = (res.data?.records || []).map((r) => ({
        id: r.id,
        date: r.createTime ? String(r.createTime).slice(0, 10) : '',
        points: r.points || 0,
        type: r.type || '',
        description: r.description || ''
      }))
    },

    async fetchPointsTotal() {
      const res = await getMyPointsTotal()
      this.memberInfo.points = res.data || 0
    },

    async updateProfile(data) {
      await updateUserProfile(data)
      await this.fetchMemberInfo()
      return true
    },

    async renewMember(months) {
      const base = this.memberInfo.expireDate ? new Date(this.memberInfo.expireDate) : new Date()
      if (Number.isNaN(base.getTime()) || base < new Date()) {
        base.setTime(Date.now())
      }
      base.setMonth(base.getMonth() + Number(months || 1))
      await updateUserProfile({
        memberLevel: this.memberInfo.levelCode || 'regular',
        memberExpireTime: base.toISOString(),
        growthValue: (this.memberInfo.growthValue || 0) + Number(months || 1) * 100
      })
      await this.fetchMemberInfo()
      return true
    }
  }
})
