import { z } from 'zod';

// ==========================================
// Authentication Schemas
// ==========================================

export const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters long'),
});

export type LoginFormData = z.infer<typeof loginSchema>;

export const registerSchema = z.object({
  fullName: z.string().min(2, 'Full name must be at least 2 characters'),
  email: z.string().email('Please enter a valid email address'),
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .regex(/[0-9]/, 'Password must contain at least one number'),
  role: z.enum(['CLIENT', 'FREELANCER']),
  phone: z.string().optional(),
});

export type RegisterFormData = z.infer<typeof registerSchema>;

// ==========================================
// Project Posting Schema
// ==========================================

export const projectPostSchema = z.object({
  title: z.string().min(5, 'Title must be at least 5 characters long').max(100),
  description: z.string().min(30, 'Description must be at least 30 characters long'),
  category: z.string().min(1, 'Category is required'),
  requiredSkills: z.array(z.string()).min(1, 'Select at least one required skill'),
  budgetMin: z.number().positive('Minimum budget must be positive'),
  budgetMax: z.number().positive('Maximum budget must be positive'),
  duration: z.string().min(1, 'Duration is required'),
  deadline: z.string().optional(),
}).refine((data) => data.budgetMax >= data.budgetMin, {
  message: 'Maximum budget must be greater than or equal to minimum budget',
  path: ['budgetMax'],
});

export type ProjectPostFormData = z.infer<typeof projectPostSchema>;

// ==========================================
// Proposal Submission Schema
// ==========================================

export const proposalSubmitSchema = z.object({
  coverLetter: z.string().min(50, 'Cover letter must be at least 50 characters long'),
  proposedBudget: z.number().positive('Proposed budget must be positive'),
  estimatedDuration: z.string().min(1, 'Estimated duration is required'),
  milestonesSuggested: z.array(
    z.object({
      title: z.string().min(3, 'Milestone title is required'),
      amount: z.number().positive('Milestone amount must be positive'),
      deadline: z.string().min(1, 'Deadline date is required'),
    })
  ).optional(),
});

export type ProposalSubmitFormData = z.infer<typeof proposalSubmitSchema>;

